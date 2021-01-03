import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BTree {
    private int t;
    private BTreeNode root;


    public BTree (String tVal){
        for(int i = 0; i < tVal.length(); i++){ // makes sure tVal is a number
            if(tVal.charAt(i) < '0' | tVal.charAt(i) > '9')
                throw new RuntimeException("Illegal tVal input");
        }
        this.t = Integer.parseInt(tVal);
        if(t < 2)
            throw new RuntimeException("t is less than 2");
        root=new BTreeNode(t);
    }

    public Pair<BTreeNode, Integer> search(BTreeNode node, String pass) {
        int i = 0;

        while (i < node.nVal() && pass.compareTo(node.getKeys()[i]) > 0) {
            i++;
        }
        if (i < node.nVal()) {
            if (pass.compareTo(node.getKeys()[i]) == 0) {
                return new Pair(node, i);
            }
        }
        if (node.isLeaf()) {
            return null;
        }
        return search(node.getChildren()[i], pass);
    }

    public Pair<BTreeNode, Integer> search(String pass) {
        return search(this.root, pass);
    }
    public void insert (String key){
        key = key.toLowerCase();
        if (root.isEmpty()){// if tree is empty, put key as root
            root = new BTreeNode(t);
            root.keys[0] = key;
            root.changeN(1);
        } else {
            if (root.nVal()==(2*t-1)){// if root is full, make new root and split old root
                BTreeNode newRoot = new BTreeNode(t);
                newRoot.changeIsLeaf(false);
                newRoot.children[0]=root;
                newRoot.splitChild(0,root);
                int i = 0;
                if (keyComparator(key, newRoot.keys[0])>0)//find child that key needs to be in
                    i++;
                newRoot.children[i].insertNonFull(key);// insert key to correct child
                root =newRoot;


            } else{// if root is not full, insert regularly
                root.insertNonFull(key);
            }
        }
    }

    public int keyComparator(String key1, String key2){ // return negative number if key 1 is bigger, positive if key2 is bigger, 0 if equal
        String lowerKey1 = key1.toLowerCase();
        String lowerKey2 = key2.toLowerCase();
        int key1Size = key1.length();
        int key2Size = key2.length();
        for ( int i = 0; i < key1Size && i < key2Size; i++){
            if ((int)lowerKey1.charAt(i)!=(int)lowerKey2.charAt(i)){
                return (int)lowerKey1.charAt(i)-(int)lowerKey2.charAt(i);
            }
        }
        if (key1Size!=key2Size){
            return key1Size-key2Size;
        }
        return 0;
    }

    public void createFullTree(String badPasswords){
        try {
            File file = new File(badPasswords);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) { //goes through each password in file
                insert(scanner.next());//inserts each password
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
    }
    public String toString(){// returns string of tree in order
        String output = "";
        if (root==null){
            return output;
        }else if (root!=null){
            output = root.toString(0);
        }
        output = output.substring(0,output.length()-1);
        return output;
    }

    public void searchWordsInTree(String requestedPasswordsProperty){
        try {
            File file = new File(requestedPasswordsProperty);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) { //goes through each password in file
                String pass = scanner.next();
                Pair<BTreeNode, Integer> node = search(pass);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
    }
    public String getSearchTime(String requestedPasswordsProperty){
        long startTime = System.nanoTime();
        searchWordsInTree(requestedPasswordsProperty);
        long endTime = System.nanoTime();

        double duration = (endTime - startTime) / 1000000.0;  //divide by 1000000 to get milliseconds.
        return Double.toString(duration);
    }
    public void delete(String k) {
        if (root.isEmpty()) {
            throw new IllegalArgumentException("tree is empty");
        }
        if (root.isLeaf() & root.nVal()==1){
            root=null;
        } else {
            if (!root.isLeaf() & root.nVal() == 1 && root.children[0].nVal() == t - 1 & root.children[1].nVal() == t - 1) {
                root.merge(0);
                root = root.children[0];
            }
            root.delete(k);
        }
    }

        public void deleteKeysFromTree (String badPasswords){
            try {
                File file = new File(badPasswords);
                Scanner scanner = new Scanner(file);
                while (scanner.hasNext()) { //goes through each password in file
                    delete(scanner.next());//inserts each password
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                System.out.println(e.toString());
            }
        }


}
