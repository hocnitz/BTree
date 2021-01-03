public class HashList<T> {


    private HashListElement<T> first;
    private int size;

    public HashList(){
        first = null;
        size = 0;
    }
    public int size(){return size;}
    public boolean isEmpty(){return first==null;}
    //insert a new link to the hashlist
    public void addFirst(T element, int key){
        if(element == null)
            throw new RuntimeException();
        HashListElement<T> newElement = new HashListElement<>(element);
        //if list is empty start a new chaining
        if(isEmpty())
            first = newElement;
        //move to the end of the list and insert the new hash list element
        else {
            HashListElement<T> curr = first;
            while (curr.getNext() != null) {
                curr = curr.getNext();
            }
            curr.setNext(newElement);
        }
        size++;

    }
    //going through the list and check if the element is in the list
    public boolean existInList(T element){
        boolean output = false;
        for(HashListElement<T> curr = first; curr != null & !output; curr = curr.getNext()){
            //if element is equal to curr it means he is inside the list
            output = element.equals(curr.getValue());
        }
        return output;
    }


}
