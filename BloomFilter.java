import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BloomFilter {

    private boolean[] binaryArray;
    private int PRIMENUMBER = 15486907;
    private Pair[] pairsArray;
    private int size;

    //Bloomfilter constructor, building a new binary array and pairArray for the hash functions.
    public BloomFilter (String m1, String hashFunctions){
        if(m1.length() == 1 && m1.charAt(0) == '0')
            throw new RuntimeException("Illegal Size input");
        for(int i = 0; i < m1.length(); i++){ // makes sure m1 is a number
            if(m1.charAt(i) < '0' | m1.charAt(i) > '9')
                throw new RuntimeException("Illegal M1 input");
        }
        size = Integer.parseInt(m1);
        binaryArray = new boolean[size];
        pairsArray = new Pair[((hashFunctionsSize(hashFunctions)))];
        readFunctions(hashFunctions);//puts hash functions alphas and betas in the pairsArray

    }
    private long hornerRuleConvert(String password){  // converts passwords to keys
        long k = password.charAt(0);
        for(int i = 1; i < password.length(); i++){
            k = password.charAt(i) + 256*(k%PRIMENUMBER);
        }
        return k % PRIMENUMBER;
    }
    private int hashFunctionsSize(String hashFunctions){//checks what size should pairs array be (number of hash function)
        int count = 0;
        try {
            File file = new File(hashFunctions);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()){
                scanner.next();
                count++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
        return count;
    }

    public void updateTable(String badPasswordsProperty){  //inserts bad passwords into the array using the hash functions
        try {
            File file = new File(badPasswordsProperty);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) { //goes through each password in file
                insertFunctionCalculation(scanner.next());//inserts each password using hash functions
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
    }
    private void insertFunctionCalculation(String password){  //calculates hash functions results and updates array according to results
        for (Pair pair : pairsArray) {  //calculates each hash function
            long k = hornerRuleConvert(password);
            int alpha = (int)pair.getAlpha();
            int beta = (int)pair.getBeta();
            long h = (((alpha * k + beta) % PRIMENUMBER) % size);  //finds index
            binaryArray[(int) h] = true;
        }
    }
    private void readFunctions(String hashFunctions){  //puts hash functions alpha and beta in pairsArray
        try {
            File file = new File(hashFunctions);
            Scanner scanner = new Scanner(file);
            int i = 0;
            while (scanner.hasNext()) {  // goes through each line and inserts into array
                String line =scanner.next();
                String[] lineHelper = line.split("_"); //split the line based on the regex "_" while alpha will be in [0] and beta in [1]
                pairsArray[i]= new Pair<Integer,Integer>(Integer.parseInt(lineHelper[0]), Integer.parseInt(lineHelper[1]));
                i++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
    }
    public Boolean contains(String pass){// checks if password is in hash table (reversing the hash functions)
        for (Pair pair : pairsArray) {
            long k = hornerRuleConvert(pass);
            int alpha = (int) pair.getAlpha();
            int beta = (int) pair.getBeta();
            long h = (((alpha * k + beta) % PRIMENUMBER) % size);
            if (!binaryArray[(int) h])
                return false;
        }
        return true;
    }
    public String getFalsePositivePercentage(HashTable hashTable, String requestedPassProperty){
        String[] requestedPassArray = rejectedPasswordsArray(requestedPassProperty);
        double count = 0;
        double requestedpassCount = 0;
        for(String pass: requestedPassArray) {
            boolean filterBloomOutput = contains(pass); //check if the requested password is inside the bloom filter
            boolean hashTableOutput = hashTable.contains(pass); //check if the requested password is inside the hash table
            if (filterBloomOutput & !hashTableOutput) //if its inside the bloom filter and not in the hash table it means the bloom filter made an error
                count++;
            if (!hashTableOutput) { //how much requested passwords should be
                requestedpassCount++;
            }
        }
        return Double.toString(count / requestedpassCount);
    }
    //counting the amount of lines in the requested password file in order to determine the size of the requested passwords array.
    private int countRequestedPasswords(String requestedPasswordsProperty){
        int count = 0;
        try {
            File file = new File(requestedPasswordsProperty);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                count++;
                scanner.next();
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
        return count;
    }
    //this function return array of strings that contains the requested passwords.
    private String[] rejectedPasswordsArray(String requestedPasswordsProperty){
        String[] passwords = new String[countRequestedPasswords(requestedPasswordsProperty)];
        int i = 0;
        try {
            File file = new File(requestedPasswordsProperty);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String pass = scanner.next();
                passwords[i] = pass;
                i++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
        return passwords;
    }
    public String getRejectedPasswordsAmount(String requestedPasswordsProperty){
        int count = 0;
        String[] requestedPasswordsArray = rejectedPasswordsArray(requestedPasswordsProperty);
        for(String pass: requestedPasswordsArray) {
            boolean filterBloomOutput = contains(pass);
            if (filterBloomOutput)
                count++;
        }
        return Integer.toString(count);

    }
}
