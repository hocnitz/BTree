import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HashTable {
    private static final double GOLD = 0.618;
    private static final int PRIMENUMBER = 15486907;
    private HashList[] hashTable;
    private int size;

    public HashTable(String m2){
        //make sure the string m2 is a real valid number
        if(m2.length() == 1 && m2.charAt(0) == '0')
            throw new RuntimeException("Illegal Size input");
        for(int i = 0; i < m2.length(); i++){ // makes sure m1 is a number
            if(m2.charAt(i) < '0' | m2.charAt(i) > '9')
                throw new RuntimeException("Illegal M1 input");
        }
        size = Integer.parseInt(m2);
        hashTable = new HashList[size];
        //in every index of the hash table insert a new empty list
        for(int i = 0;i < hashTable.length; i++){
            hashTable[i] = new HashList();
        }
    }
    //convert a string to a number using horner rule
    private long hornerRuleConvert(String password){
        long k = password.charAt(0);
        for(int i = 1; i < password.length(); i++){
            k = password.charAt(i) + 256*(k%PRIMENUMBER);
        }
        return k % PRIMENUMBER;
    }
    //the hash function we used on class
    public int HashFunction(long k){
        return (int) k%size;
    }

    //insert the element inside the key index (after we used the hashfunction on the key)
    public void startChain(int hashKey, Object element){
        if(hashTable[hashKey] == null)
            hashTable[hashKey] = new HashList();
        hashTable[hashKey].addFirst(element, hashKey);
    }

    //read the lines from the bad passwords text file and inserting them to the hash table
    public void updateTable(String badPasswordsProperty){
        try {
            File file = new File(badPasswordsProperty);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                //converting the password to number
                long k = hornerRuleConvert(scanner.next());
                //using the function on the key and inserting the password number value to that key using startChain
                startChain(HashFunction(k), k);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Illegal Hash Functions File");
        }
    }
    //check if the password is inside the hash table
    public boolean contains(String element){
        long password = hornerRuleConvert(element);
        int hashCode = HashFunction(password);
        return hashTable[hashCode].existInList(password);
    }
    //searching if the requested passwords are inside the hash table
    public void searchWordsInHashTable(String requestedPasswordsProperty){
        try {
            File file = new File(requestedPasswordsProperty);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) { //goes through each password in file
                String pass = scanner.next();
                //check if the word is inside the table
                Boolean wordIsInTable = contains(pass);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
    }
    public String getSearchTime(String requestedPasswordsProperty){
        long startTime = System.nanoTime();
        searchWordsInHashTable(requestedPasswordsProperty); //running the search function
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1000000.0;  //divide by 1000000 to get milliseconds.
        return Double.toString(duration);
    }
}
