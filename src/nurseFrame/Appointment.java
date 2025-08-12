package nurseFrame;

public class Appointment {
    private int appointmentId;
    private String visitDatetime;

    public Appointment(int appointmentId, String visitDatetime) {
        this.appointmentId = appointmentId;
        this.visitDatetime = visitDatetime;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public String getVisitDatetime() {
        return visitDatetime;
    }

    @Override
    public String toString() {
        return visitDatetime;  // 버튼 라벨로 사용
    }
}
