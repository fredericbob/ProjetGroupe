package projet.Resultat;

public class Resultat {

    private String message;
    private String error;
    private Object data;

    public Resultat(String message, String error, Object data) {
        this.setMessage(message);
        this.setError(error);
        this.setData(data);
    }

    public Resultat() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
