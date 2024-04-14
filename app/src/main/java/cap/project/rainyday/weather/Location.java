package cap.project.rainyday.weather;

public class Location {
    public int nx;
    public int ny;
    public String regId;

    public Location()
    {
        regId = null;
    }

    public Location(int _nx, int _ny, String _regId)
    {
        nx = _nx;
        ny = _ny;
        regId = _regId;
    }

}
