import java.util.*;

void main() {
    Scanner scanner = new Scanner(System.in);
    Map<String, List<Character>> map = null;
    List<Character> closure = null;
    boolean inputFD = true;
    while (inputFD) {
        System.out.println("Please input a list of Functional Dependencies, separated by commas: ");
        String fdString = scanner.nextLine().toUpperCase();
        //this if statement might need to be moved to the function
        if (!fdString.isEmpty()) {
            map = simplifyAndMap(fdString);
            if(map != null) {
                inputFD = false;
            }
        } else {
            System.out.println("ERROR: Invalid input, please try again");
        }
    }
    boolean inputClosure = true;
    while (inputClosure){
        System.out.println("Input attributes of closure to compute: ");
        String clString = scanner.nextLine().toUpperCase();
        if (!clString.isEmpty()) {
                closure = computeClosure(clString, map);
                if(closure != null) {
                    System.out.printf("Closure: %s", closure);
                    inputClosure = false;
                }
        } else {
            System.out.println("ERROR: Invalid input, please try again");
        }

    }
}

//program must take as in put a string of functional dependencies, separated by commas.
//function inputs LHS as key to Map, and its individual RHS attributes as its value
Map<String, List<Character>> simplifyAndMap(String fd){
    //List<String> stripped = new ArrayList<>();
    Map<String, List<Character>> FDColl = new HashMap<>();
    List<Character> charList;
    String[] stripped;
    String[] FDs = fd.split(",");
    for (String elmt : FDs){
        //rm whitespace
        elmt = elmt.strip();
        //if elmt doesnt have -> or does not contain a RHS or LHS, throw error
        if(!elmt.matches("[A-Z]+->[A-Z]+")){
            System.out.printf("ERROR: Invalid input at \"%s\" , please try again\n", elmt);
            FDColl.clear();
            return null;
        }
        //separates string by '->', leaving lhs and rhs
        stripped = elmt.split("->");
        //make sure that each side of the FD are alphabetic
        if(stripped[0].matches("^[a-zA-Z]+$") && stripped[1].matches("^[a-zA-Z]+$")) {
            charList = new ArrayList<>();
            //for every subsequent char in the RHS, put it in the hashmap with the LHS as the key, and the corresponding chars as RHS.
            for (int i = 0; i < stripped[1].length(); i++) {
                charList.add(stripped[1].charAt(i));
            }
            FDColl.computeIfAbsent(stripped[0], k -> new ArrayList<>())
                    .addAll(charList);
        }
        else{
            System.out.println("ERROR: Invalid input, must be alphabetic");
            FDColl.clear();
            return null;
        }
    }
    return FDColl;
}
//takes as input desired attributes, and returns the closure for said attributes.
List<Character> computeClosure(String att, Map<String, List<Character>> map){
    List<Character> tempList = new ArrayList<>();
    List<Character> closure = new ArrayList<>();
    //in case the user inputs more than one character, make into an alphabetic array of chars, sorted
    char[] temp = att.matches("^[a-zA-Z]+$")? att.strip()
                                                        .chars()
                                                        .sorted()
                                                        .mapToObj(c -> String.valueOf((char) c))
                                                        .distinct()
                                                        .collect(Collectors.joining()).toCharArray(): null;
    if(temp == null){
        System.out.println("ERROR: Invalid input, must be alphabetic");
        return null;
    }

    for (char c : temp) {
        if (!closure.contains(c)) {
            closure.add(c);
        }
    }
    boolean changed = true;

    while (changed) {
        changed = false;

        // Check each functional dependency
        for (String lhs : map.keySet()) {
            boolean containsAll = true;

            // Check whether all LHS attributes are in closure, add them all to closure if not
            for (char c : lhs.toCharArray()) {
                if (!closure.contains(c)) {
                    containsAll = false;
                    break;
                }
            }

            // Check all attributes now in closure, If LHS is contained, add RHS attributes
            if (containsAll) {
                for (char c : map.get(lhs)) {
                    if (!closure.contains(c)) {
                        closure.add(c);
                        changed = true;
                    }
                }
            }
        }
    }

    //Return the completed closure
    closure.sort(null);
    return closure;
}



//    for(int i = 0; i < temp.length; i ++){
//        if(map.containsKey(temp[i])){
//            //gets the array list at the given key, iterates through list and adds attributes to the closure.
//            tempList.addAll(map.get(temp[i]));
//            closure.addAll(tempList.stream()
//                    .distinct()
//                    .toList());
//        }
//    }
//
//}
//
//destinationList.addAll(
//        sourceList.stream()
//              .filter(item -> /* condition */)
//        .collect(Collectors.toList())
//        );

//AB->CE  =>  AB -> C

//grab the LHS, the following