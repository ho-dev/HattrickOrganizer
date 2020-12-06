package module.youth;

import core.model.player.YouthPlayer;

import javax.swing.*;

public class SkillInfoColumn extends JSlider {

    YouthPlayer.SkillInfo skillInfo;

    public SkillInfoColumn(YouthPlayer.SkillInfo info){
        this.skillInfo = info;

        this.setToolTipText(info.toString());
    }
}
