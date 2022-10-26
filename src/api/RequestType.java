package api;

//TODO robustify
public enum RequestType {
    ISBN, AUTHORS;

    public String toString(){
        return super.toString().toLowerCase();
    }
}
