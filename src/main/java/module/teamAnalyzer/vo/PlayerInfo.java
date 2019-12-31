package module.teamAnalyzer.vo;

import module.teamAnalyzer.manager.PlayerDataManager;

public class PlayerInfo {
    //~ Instance fields ----------------------------------------------------------------------------
    String name = "";
    int age;
    int experience;
    int form;
    int playerId;
    int specialEvent;
    int status = PlayerDataManager.UNKNOWN;
    int tSI;
    int teamId;
    int salary; // Money in SEK
    int stamina;
    boolean motherClubBonus;

    //~ Methods ------------------------------------------------------------------------------------
    public void setAge(int i) {
        age = i;
    }

    public int getAge() {
        return age;
    }

    public void setExperience(int i) {
        experience = i;
    }

    public int getExperience() {
        return experience;
    }

    public void setForm(int i) {
        form = i;
    }

    public int getForm() {
        return form;
    }

    public void setName(String string) {
        name = string;
    }

    public String getName() {
        return name;
    }

    public void setPlayerId(int i) {
        playerId = i;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setSpecialEvent(int i) {
        specialEvent = i;
    }

    public int getSpecialEvent() {
        return specialEvent;
    }

    public void setStatus(int i) {
        status = i;
    }

    public int getStatus() {
        return status;
    }

    public void setTSI(int i) {
        tSI = i;
    }

    public int getTSI() {
        return tSI;
    }

    public void setTeamId(int i) {
        teamId = i;
    }

    public int getTeamId() {
        return teamId;
    }

    /**
     * toString methode: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("PlayerInfo[");
        buffer.append("name = " + name);
        buffer.append(", age = " + age);
        buffer.append(", experience = " + experience);
        buffer.append(", form = " + form);
        buffer.append(", playerId = " + playerId);
        buffer.append(", specialEvent = " + specialEvent);
        buffer.append(", status = " + status);
        buffer.append(", tSI = " + tSI);
        buffer.append(", teamId = " + teamId);
        buffer.append(", salary = " + salary);
        buffer.append(", stamina = " + stamina);
        buffer.append(", motherClubBonus = " + motherClubBonus);
        buffer.append("]");
        return buffer.toString();
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
    public int getSalary() {
        return salary;
    }
    public void setStamina(int stamina) { this.stamina = stamina; }
    public int getStamina() { return this.stamina; }
    public void setMotherClubBonus(boolean bonus) { this.motherClubBonus = bonus; }
    public boolean getMotherClubBonus() { return this.motherClubBonus; }
}
