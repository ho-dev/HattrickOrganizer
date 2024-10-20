package module.lineup.substitution;

import core.model.TranslationFacility;
import core.util.GUIUtils;
import module.lineup.Lineup;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.Substitution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SubstitutionEditDialog extends JDialog {

	private static final long serialVersionUID = 1875761460780943159L;
	private MatchOrderType orderType;
	private SubstitutionEditView behaviourView;
	private boolean canceled = true;

	public SubstitutionEditDialog(Dialog parent, MatchOrderType orderType) {
		super(parent, true);
		this.orderType = orderType;
		initDialog();
	}

	public SubstitutionEditDialog(Frame parent, MatchOrderType orderType) {
		super(parent, true);
		this.orderType = orderType;
		initDialog();
	}

	public void init(Lineup lineup, Substitution sub) {
		this.orderType = sub.getOrderType();
		setDlgTitle();
		this.behaviourView.init(lineup, sub);
	}

	public boolean isCanceled() {
		// confirmed
		if(!canceled) behaviourView.ratingRecalc();
		return this.canceled;
	}

	private void initDialog() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setDlgTitle();
		initComponents();
		pack();
	}

	private void setDlgTitle() {
		String dlgTitleKey = switch (this.orderType) {
			case NEW_BEHAVIOUR -> "subs.TypeOrder";
			case SUBSTITUTION -> "subs.TypeSub";
			case POSITION_SWAP -> "subs.TypeSwap";
			case MAN_MARKING -> "subs.TypeManMarking";
		};
		setTitle(TranslationFacility.tr(dlgTitleKey));
	}

	private void initComponents() {
		getContentPane().setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		JButton okButton = new JButton(TranslationFacility.tr("ls.button.ok"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(12, 8, 8, 2);
		buttonPanel.add(okButton, gbc);

		JButton cancelButton = new JButton(TranslationFacility.tr("ls.button.cancel"));
		gbc.gridx = 1;
		gbc.weightx = 0.0;
		gbc.insets = new Insets(12, 2, 8, 8);
		buttonPanel.add(cancelButton, gbc);

		this.behaviourView = new SubstitutionEditView(this.orderType);
		getContentPane().add(this.behaviourView, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				canceled = false;
				dispose();
			}
		});

		Action cancelAction = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				canceled = true;
				dispose();
			}

		};
		cancelAction.putValue(Action.NAME, TranslationFacility.tr("ls.button.cancel"));
		cancelButton.setAction(cancelAction);
		GUIUtils.decorateWithActionOnESC(this, cancelAction);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				canceled = true;
			}
		});

		GUIUtils.equalizeComponentSizes(okButton, cancelButton);
	}
}
