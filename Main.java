package pl.asseco.prk.pmpplus.ca.srv.ca.service.save;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        var capacity = 3;
        var items = createItems();
        findLargestSum(items, capacity);
    }



    static Set<Item> findLargestSum(Set<Item> items, int initialCapacity) {
        if(items.isEmpty()){
            throw new IllegalArgumentException("Set should not be empty");
        }
        if (items.size() == 1){
            return items;
        }

        var itemsByValueDesc = items.stream().sorted((i1, i2) -> i2.getValue() - i1.getValue()).collect(Collectors.toList());
        var left1WeightItems = new ArrayList<Item>();
        var usedCapacity = 0;
        var resultList = new ArrayList<Item>();
        for (Item item : itemsByValueDesc) {
            if (initialCapacity - usedCapacity < 1) {
                if (item.getWeight() == 1) {
                    left1WeightItems.add(item);
                }
                continue;
            }
            if (initialCapacity - usedCapacity == 1) {
                resultList.add(itemsByValueDesc.stream().filter(i -> i.getWeight() == 1).findFirst().get());
                usedCapacity += item.getWeight();
                continue;
            }
            resultList.add(item);
            usedCapacity += item.getWeight();
        }

        try {
            if(left1WeightItems.size() >= 2){
                swapItemsWithHigherValue(left1WeightItems, resultList);
            }
        } catch (IndexOutOfBoundsException e ){
            e.getStackTrace();
        }

         return resultList.stream().collect(Collectors.toSet());
    }

    private static void swapItemsWithHigherValue(ArrayList<Item> left1WeightItems, ArrayList<Item> resultList) {
        var lowestValue2WeightItem = getLowestValue2WeightItem(resultList);
        var highestValue1WeightItem = left1WeightItems.get(0);
        var secondHighestValue1WeightItem = left1WeightItems.get(1);
        while (lowest2WeightItemHasLessValueThan2Highest1WeightItems(highestValue1WeightItem,secondHighestValue1WeightItem, lowestValue2WeightItem)){
            if(lowestValue2WeightItem.getValue() == 0){
                return;
            }
            resultList.remove(lowestValue2WeightItem);

            resultList.add(secondHighestValue1WeightItem);
            resultList.add(highestValue1WeightItem);

            left1WeightItems.remove(highestValue1WeightItem);
            left1WeightItems.remove(secondHighestValue1WeightItem);

            lowestValue2WeightItem = getLowestValue2WeightItem(resultList);
            highestValue1WeightItem = left1WeightItems.get(0);
            secondHighestValue1WeightItem = left1WeightItems.get(1);


        }
    }

    private static boolean lowest2WeightItemHasLessValueThan2Highest1WeightItems(Item highestValue1WeightItem, Item secondHighestValue1WeightItem, Item lowestValue2WeightItem) {
        return highestValue1WeightItem.getValue() + secondHighestValue1WeightItem.getValue() > lowestValue2WeightItem.getValue();
    }

    private static Item getLowestValue2WeightItem(ArrayList<Item> resultList) {
        return resultList.stream().filter(item -> item.getWeight() == 2).min(Comparator.comparing(Item::getValue)).orElseGet(() -> new Item((short) 2,0));
    }

    private static Set<Item> createItems() {
        Set<Item> items = new HashSet<>();

//        short[] weights = {1, 1, 1, 1, 1, 1};
//        int[] values = {10, 15, 16, 11, 12, 7};

        short[] weights = {1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2};
        int[] values = {10, 20, 10, 11, 12, 12, 8, 17, 11, 8, 7, 5, 5, 2, 17, 15, 14, 20, 22, 21};

        for (int i = 0; i < weights.length; i++) {
            items.add(new Item(weights[i], values[i]));
        }
        return items;
    }


}

class Item {

    private short weight;

    private int value;

    Item(short weight, int value) {
        if (weight < 1 || weight > 2) {
            throw new IllegalArgumentException("Weight must be between 1 and 2");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative number");
        }
        this.weight = weight;
        this.value = value;
    }

    public short getWeight() {
        return weight;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return weight == item.weight && value == item.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight, value);
    }
}