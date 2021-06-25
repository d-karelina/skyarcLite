package ru.aeroscript.skyarclite.zones;

public abstract class Limit {
    private Units units ;
    private VerticalReference verticalReference ;
    private int value ;

}
enum Units{
    NOTSP, // unspecified (не применимо)
    M, // meters
    KM, // kilometers
    FT, // feet
    NM, // nautical miles (не применимо)
    DEG, // degree (не применимо)
    RAD // radian (не применимо)

}

enum VerticalReference{
    MSL, // Above mean sea level
    AGL // Above ground level (учитываем как MSL)

}