package core;


// Using for map a grid data

class Mapping <T, U> {
    private T first;
    private U second;

    public Mapping () { 
        this.first  = null;
        this.second = null;
    }

    public Mapping (T first, U second) {
        this.first  = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    public void set(T first, U second) {
        this.first  = first;
        this.second = second;
    }
}
