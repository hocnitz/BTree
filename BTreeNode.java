public class BTreeNode {
    private int n; // current number of keys in node
    private boolean isLeaf; //true if node is a leaf
    public String[] keys; // array containing the keys
    private int t; // defines the range for the number of keys in a node
    public BTreeNode[] children; // array containing child pointers

    public BTreeNode (int t){
        this.t=t;
        keys = new String[(2*t-1)]; // makes keys array maximum size
        children = new BTreeNode[(2*t)]; // makes children array maximum size
        n=0;
        isLeaf=true;
    }

    public int nVal(){return n;}
    public boolean isEmpty(){ return (n==0);}
    public void changeIsLeaf(boolean isLeaf){ this.isLeaf=isLeaf;}
    public void changeN(int n){this.n=n;}
    public boolean isLeaf(){ return this.isLeaf;}
    public String[] getKeys() { return keys; }
    public BTreeNode[] getChildren() { return children; }

    public void insertNonFull(String k) {// insert k into node that has less than 2*t-1 keys
        int i = n - 1;
        if (isLeaf) {// if node is leaf, only keys array needs to be changed
            while (i>=0 && keyComparator(k, keys[i])<0) {// move all keys bigger than k to the right in keys array
                keys[i + 1] = keys[i];
                i--;
            }
            keys[i + 1] = k;//put k right after first index that has a number smaller then k
            n = n + 1;// update field n of node
        } else {// if node if not leaf, find child that k needs to be inserted to
            while (i>=0 && keyComparator(k, keys[i])<0) {// find index of key smaller than k
                i--;
            }
            if (children[i + 1].nVal() == (2 * t - 1)) {// if child is full, split it
                splitChild(i + 1, children[i + 1]);
                if (keyComparator(k, keys[i+1])>0 )// check between two new children, which should k be in
                    i++;
            }
            children[i + 1].insertNonFull(k);// insert k in correct child
        }
    }

    public int searchIndex(String k) {//search for key k in subtree of current node
        for (int i=0;i<n;i++){
            if(keys[i].equals(k))
                return i;
        }
        return -1;
    }


    public int keyComparator(String key1, String key2){ // return negative number if key1 is bigger, positive if key2 is bigger, 0 if equal

        String lowerKey1 = key1.toLowerCase();
        String lowerKey2 = key2.toLowerCase();
        int key1Size = key1.length();
        int key2Size = key2.length();
        for ( int i = 0; i < key1Size && i < key2Size; i++){// goes through each char of both strings
            if ((int)lowerKey1.charAt(i)!=(int)lowerKey2.charAt(i)){// if char i of strings is not equal, returns result
                return (int)lowerKey1.charAt(i)-(int)lowerKey2.charAt(i);
            }
        }
        if (key1Size!=key2Size){//if all chars were the same, the longer word is bigger
            return key1Size-key2Size;
        }
        return 0;
    }

    public void splitChild(int i, BTreeNode x) { //splits the child (node x) of current node
        moveRight(i);
        BTreeNode sibling = new BTreeNode(t); // new node which will contain all keys of x
        sibling.changeN(t - 1);
        sibling.changeIsLeaf(x.isLeaf());
        for (int j=t; j < 2*t-1; j++){ // copy second half to sibling
            sibling.keys[j-t] = x.keys[j];
            x.keys[j]=null;
            if(!x.isLeaf()){
                sibling.children[j-t]=x.children[j];
                x.children[j]=null;
            }
        }
        if(!x.isLeaf()){
            sibling.children[t-1]=x.children[2*t-1];
            x.children[2*t-1]=null;
        }
        keys[i]=x.keys[t-1];
        x.keys[t-1]=null;
        x.changeN(t-1);// reduce number of keys in x to half
        children[i+1]=sibling;//puts new child in node
    }
    public String toString(int depth){// creates an in-order string of all keys in tree
        String output= "";
        for (int i=0; i<n; i++){//goes through all keys and children on arrays, in order
            if(!isLeaf)//if node isn't a leaf, add child to string first and then add key, otherwise, add key only
                output=output+children[i].toString(depth+1);
            output=output+keys[i]+"_"+depth+",";
        }
        if (!isLeaf) {//adds last child to string if node is not a leaf
            output = output + children[n].toString(depth + 1);
        }
        return output;
    }

    public void delete(String k){
        int idx = searchIndex(k);
        if (idx>=0){//if key is in node x
            if(isLeaf() && nVal() >= t){//if x is leaf, delete key from node
                moveLeft(idx);
            } else if (children[idx].nVal()>=t){//if x is no leaf and left to key child has at least t keys, replace key with biggest key of child
                String tmp = children[idx].keys[children[idx].nVal()-1];
                keys[idx]=tmp;
                children[idx].delete(children[idx].keys[children[idx].nVal()-1]);
            } else if (children[idx+1].nVal()>=t){
                String tmp = children[idx+1].keys[0];
                keys[idx]=tmp;
                children[idx+1].delete(tmp);
            } else {// if both children have t-1 keys, merge them and delete key
                merge(idx);
                children[idx].delete(k);
            }
        } else { // if key is not in x
            deleteFromChild(k);
        }
    }

    private void deleteFromChild(String k){
        int i =0;
        while (i<nVal() && keyComparator(k,keys[i])>=0)//finds index of first key bigger than k (index of child containing k)
            i++;
        if (children[i].nVal()==t-1){//if child has minimum keys, fix it and then delete k from it
            if (i==0){//if child has only right sibling, borrow from it
                if (children[1].nVal()>=t){
                    borrowFromRight(i);
                } else {
                    merge(i);
                }
            } else if (i==nVal()) {//if child has only left sibling
                if(children[i-1].nVal()>=t){
                    borrowFromLeft(i);
                } else {
                    merge(i-1);
                    i--;
                }
            } else {//if child has both siblings
                if (children[i-1].nVal()>=t){//if left sibling has at least t keys
                    borrowFromLeft(i);
                } else if (children[i+1].nVal()>=t){//if right sibling has at least t keys
                    borrowFromRight(i);
                } else {//if both siblings have t-1 keys
                    merge(i);
                }
            }
        }
            children[i].delete(k);
    }

    public void merge(int index){//merges child index with right sibling and key index
        children[index].keys[t-1]=keys[index];
        for(int i = 0; i<t-1;i++){
            children[index].keys[i+t]=children[index+1].keys[i];
        }
        if(!children[index].isLeaf()){
            for(int i = 0; i<=t-1; i++){
                children[index].children[i+t]=children[index+1].children[i];
            }
        }
        children[index].changeN(2*t-1);
        while(index<n-1){
            keys[index]=keys[index+1];
            children[index+1]=children[index+2];
            index++;
        }
        keys[n-1]=null;
        children[n]=null;
        n=n-1;
    }

    public void borrowFromRight(int i){//borrows key from i+1 child to node and from node to child i
        children[i].keys[t-1]=keys[i];
        keys[i]=children[i+1].keys[0];
        if(!children[i].isLeaf()){
            children[i].children[t]=children[i+1].children[0];
        }
        children[i].changeN(t);
        children[i+1].moveLeft(0);
    }

    public void borrowFromLeft(int i){//borrows key from i-1 child to node and from node to child i
        children[i].moveRight(0);
        children[i].keys[0]=keys[i-1];
        keys[i-1]=children[i-1].keys[children[i-1].nVal()-1];
        if(!children[i].isLeaf()){
            children[i].children[0]=children[i-1].children[children[i-1].nVal()];
        }
        children[i-1].keys[children[i-1].nVal()-1]=null;
        children[i-1].children[children[i-1].nVal()]=null;
        children[i-1].changeN(children[i-1].nVal()-1);
    }

    public void moveLeft(int i){//move all keys and children after i to the left
        if (isLeaf){
            while(i<n-1){
                keys[i]=keys[i+1];
                i++;
            }
        } else {
            while (i<n-1){
                keys[i]=keys[i+1];
                children[i]=children[i+1];
                i++;
            }
            children[n-1]=children[n];
            children[n]=null;
        }
        keys[i]=null;
        n=n-1;
    }

    public void moveRight(int i){//move all keys and children after i to the Right
        if (isLeaf){
            while(i<=n-1){
                keys[i+1]=keys[i];
                i++;
            }
        } else {
            while (i<=n-1){
                keys[i+1]=keys[i];
                children[i+1]=children[i];
                i++;
            }
            children[n+1]=children[n];
        }
        n=n+1;
    }
}
