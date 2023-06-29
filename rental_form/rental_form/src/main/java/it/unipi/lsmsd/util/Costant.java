package it.unipi.lsmsd.util;

public final class Costant {

    public enum RequestCategory{TICKET,REPORT};
    public enum SupportStatus{OPEN,TAKEN,CLOSED};
    public enum UserCategory{Admin,User,BusinessActivity};
    public static String business_patt="@Business";
    public static String admin_patt= "@Admin";
    public String Current_user;
    public enum NotificationType {ALL,REPORT,TICKET};
    public enum TicketCategory{HELP,FRAUD,GENERIC};
    public enum VehicleCategory{car,scooter};

    private Costant(){}

}
