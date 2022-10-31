package api;

//TODO robustify
public enum RequestType {
    BOOKS, WORKS, ISBN, AUTHORS;

    public String toString(){
        return super.toString().toLowerCase();
    }
}
