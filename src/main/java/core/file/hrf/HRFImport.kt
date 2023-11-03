package core.file.hrf

import core.db.DBManager
import core.file.ExtensionFileFilter
import core.gui.HOMainFrame
import core.gui.InfoPanel
import core.gui.RefreshManager
import core.model.HOVerwaltung
import core.model.UserParameter
import core.util.HODateTime
import core.util.HOLogger
import core.util.Helper
import java.awt.Component
import java.io.File
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Optional
import java.util.function.Consumer
import javax.swing.JCheckBox
import javax.swing.JFileChooser
import javax.swing.JOptionPane

/**
 * Imports selected HRF files.
 */
class HRFImport(val frame: HOMainFrame) {

    fun hrfImport() {

        val files = getHRFFiles()
        files.ifPresent(Consumer {
            val hrfFiles = it.map { file: File ->
                if (!file.path.endsWith(".hrf"))
                    File(file.absolutePath + ".hrf")
                else file
            }.toList()

            var choice: UserChoice? = null
            for (f in hrfFiles) {
                if (!f.exists()) {
                    frame.setInformation(
                        HOVerwaltung.instance().getLanguageString("DateiNichtGefunden"),
                        InfoPanel.FEHLERFARBE
                    )
                    Helper.showMessage(
                        frame,
                        HOVerwaltung.instance().getLanguageString("DateiNichtGefunden"),
                        HOVerwaltung.instance().getLanguageString("Fehler"),
                        JOptionPane.ERROR_MESSAGE
                    )
                    return@Consumer
                }

                // Remember path
                UserParameter.instance().hrfImport_HRFPath = f.parentFile.absolutePath

                // FIXME: These setInformation should be triggered through events.
                frame.setInformation(HOVerwaltung.instance().getLanguageString("StartParse"))
                var homodel = HRFFileParser.parse(f)
                if (homodel != null && homodel.getBasics().teamId != HOVerwaltung.instance().model.getBasics().teamId) {
                    HOLogger.instance().error(
                        javaClass,
                        "hrf file from other team can not be imported: ${homodel.getBasics().teamName}"
                    )
                    homodel = null
                }

                if (homodel == null) {
                    frame.setInformation(
                        "${
                            HOVerwaltung.instance().getLanguageString("Importfehler")
                        } : ${f.getName()}", InfoPanel.FEHLERFARBE
                    )
                    Helper.showMessage(
                        frame,
                        HOVerwaltung.instance().getLanguageString("Importfehler"),
                        HOVerwaltung.instance().getLanguageString("Fehler"),
                        JOptionPane.ERROR_MESSAGE
                    )
                } else {
                    frame.setInformation(HOVerwaltung.instance().getLanguageString("HRFSave"))

                    // file already imported?
                    val hrfs = homodel.getBasics().datum.toDbTimestamp()
                    val storedHrf = DBManager.loadHRFDownloadedAt(hrfs)

                    if (choice == null || !choice.applyToAll) {
                        choice = confirmUserChoices(frame, hrfs, storedHrf)
                        if (choice.cancel) {
                            break
                        }
                    }

                    if (choice.importHRF) {
                        if (storedHrf != null) {
                            DBManager.deleteHRF(storedHrf.hrfId)
                        }
                        homodel.saveHRF()
                        val training = homodel.getTraining()
                        if (training != null) {
                            DBManager.saveTraining(training, HODateTime.now())
                        }
                        frame.setInformation(HOVerwaltung.instance().getLanguageString("HRFErfolg"))
                    } else {
                        // Cancel
                        frame.setInformation(
                            HOVerwaltung.instance().getLanguageString("HRFAbbruch"),
                            InfoPanel.FEHLERFARBE
                        )
                    }
                }
            }

            HOVerwaltung.instance().loadLatestHoModel()
            val hom = HOVerwaltung.instance().model
            HOLogger.instance().info(javaClass, "HOModel loaded: ${hom.id}")

            RefreshManager.doReInit()
        })
    }

    private fun getHRFFiles(): Optional<Array<out File>> {
        val hoAdmin = HOVerwaltung.instance()

        val fileChooser = JFileChooser()
        fileChooser.setMultiSelectionEnabled(true)
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG)
        fileChooser.setDialogTitle(hoAdmin.getLanguageString("ls.menu.file.importfromhrf"))

        val pfad = File(UserParameter.instance().hrfImport_HRFPath)

        if (pfad.exists() && pfad.isDirectory()) {
            fileChooser.setCurrentDirectory(File(UserParameter.instance().hrfImport_HRFPath))
        }

        val filter = ExtensionFileFilter()
        filter.addExtension("hrf")
        filter.setDescription(hoAdmin.getLanguageString("filetypedescription.hrf"))
        fileChooser.setFileFilter(filter)

        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            return Optional.of(fileChooser.selectedFiles)
        }
        return Optional.empty()
    }

    private fun confirmUserChoices(parent: Component, hrfDate: Timestamp, oldHRF: HRF?): UserChoice {
        val choice = UserChoice()

        val dateFormat = SimpleDateFormat("dd MMMM yyyy")
        var text = HOVerwaltung.instance().getLanguageString("HRFfrom") + " " + dateFormat.format(hrfDate)

        if (oldHRF != null) {
            text += "\n(" + HOVerwaltung.instance().getLanguageString("HRFinDB") + " " + oldHRF.name + ")"
        }

        text += "\n" + HOVerwaltung.instance().getLanguageString("ErneutImportieren")

        val applyToAllCheckBox = JCheckBox(HOVerwaltung.instance().getLanguageString("hrfImport.applyToAll"))
        val o: Array<Any> = arrayOf(text, applyToAllCheckBox)
        val value: Int = JOptionPane.showConfirmDialog(
            parent,
            o,
            HOVerwaltung.instance().getLanguageString("confirmation.title"),
            JOptionPane.YES_NO_CANCEL_OPTION
        )

        if (value == JOptionPane.CANCEL_OPTION) {
            choice.cancel = true
        } else {
            choice.applyToAll = applyToAllCheckBox.isSelected
            if (value == JOptionPane.YES_OPTION) {
                choice.importHRF = true
            }
        }
        return choice
    }

    private class UserChoice {
        var importHRF: Boolean = false
        var applyToAll: Boolean = false
        var cancel: Boolean = false
    }
}