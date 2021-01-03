public class HashListElement<T> {
    private T value;
    private HashListElement<T> next;


    public HashListElement(T value){
        if(value == null)
            throw new IllegalArgumentException();
        this.value = value;
        this.next = null;
    }
    public void setNext(HashListElement<T> next){this.next = next;}
    public HashListElement<T> getNext(){return next;}
    public T getValue(){return value;}
}
