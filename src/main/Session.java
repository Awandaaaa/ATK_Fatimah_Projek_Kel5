package main;

public class Session {
    private static String idUser;
    private static String nama;
    private static String role;

    public static void setIdUser(String idUser) {
        Session.idUser = idUser;
    }

    public static String getIdUser() {
        return idUser;
    }

    public static void setNama(String n) {
        Session.nama = n;
    }

    public static String getNama() {
        return nama;
    }

    public static void setRole(String r) {
        Session.role = r;
    }

    public static String getRole() {
        return role;
    }
}


