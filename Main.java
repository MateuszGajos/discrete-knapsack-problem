package pl.asseco.prk.pmpplus.ca.srv.ca.service.save;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        var capacity = 3;
        var items = createItems();
        findLargestSum(items, capacity);
    }

    // https://en.wikipedia.org/wiki/Knapsack_problem, but decided to go for own algorithm
    // which finds an optimal solution
    static Set<Item> findLargestSum(Set<Item> items, int initialCapacity) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Set should not be empty");
        }
        if (items.size() == 1) {
            return items;
        }
        // O(nlogn)
        var itemsByValueDesc
            = items.stream().sorted((i1, i2) -> i2.getValue() - i1.getValue()).collect(Collectors.toList());
        var itemsWithWeight1Left = new ArrayList<Item>();
        var usedCapacity = 0;
        var resultList = new ArrayList<Item>();

        for (Item item : itemsByValueDesc) {
            if (initialCapacity - usedCapacity < 1) {
                if (item.isOfWeight(1)) {
                    itemsWithWeight1Left.add(item);
                }
                continue;
            }
            if (initialCapacity - usedCapacity == 1) {
                resultList.add(itemsByValueDesc.stream().filter(i -> i.isOfWeight(1)).findFirst().get());
                usedCapacity += item.getWeight();
                continue;
            }
            resultList.add(item);
            usedCapacity += item.getWeight();
        }

        // O(n^2) - bottleneck is here, but we have a guarantee of finding an optimal solution
        swapItemsWithHigherValue(itemsWithWeight1Left, resultList);

        return resultList.stream().collect(Collectors.toSet());
    }

    private static void swapItemsWithHigherValue(ArrayList<Item> itemsWithWeight1Left, ArrayList<Item> resultList) {
        if (itemsWithWeight1Left.size() < 2) {
            return;
        }

        if (resultList.stream().noneMatch(item -> item.isOfWeight(2))) {
            return;
        }
        var lowestValue2WeightItem = getLowestValue2WeightItem(resultList);
        var highestValue1WeightItem = itemsWithWeight1Left.get(0);
        var secondHighestValue1WeightItem = itemsWithWeight1Left.get(1);

        while (lowestValue2WeightItem.isPresent()
            && lowest2WeightItemHasLessValueThan2Highest1WeightItems(highestValue1WeightItem,
                secondHighestValue1WeightItem, lowestValue2WeightItem.get())) {

            swapItems(itemsWithWeight1Left, resultList, lowestValue2WeightItem.get(), highestValue1WeightItem,
                secondHighestValue1WeightItem);

            lowestValue2WeightItem = getLowestValue2WeightItem(resultList);
            if (itemsWithWeight1Left.size() < 2) {
                break;
            }
            highestValue1WeightItem = itemsWithWeight1Left.get(0);
            secondHighestValue1WeightItem = itemsWithWeight1Left.get(1);
        }
    }

    private static void swapItems(ArrayList<Item> itemsWithWeight1Left, ArrayList<Item> resultList,
        Item lowestValue2WeightItem, Item highestValue1WeightItem, Item secondHighestValue1WeightItem) {
        resultList.remove(lowestValue2WeightItem);

        resultList.add(secondHighestValue1WeightItem);
        resultList.add(highestValue1WeightItem);

        itemsWithWeight1Left.remove(highestValue1WeightItem);
        itemsWithWeight1Left.remove(secondHighestValue1WeightItem);
    }

    private static boolean lowest2WeightItemHasLessValueThan2Highest1WeightItems(Item highestValue1WeightItem,
        Item secondHighestValue1WeightItem, Item lowestValue2WeightItem) {
        return highestValue1WeightItem.getValue() + secondHighestValue1WeightItem.getValue() > lowestValue2WeightItem
            .getValue();
    }

    private static Optional<Item> getLowestValue2WeightItem(ArrayList<Item> resultList) {
        return resultList.stream().filter(item -> item.getWeight() == 2).min(Comparator.comparing(Item::getValue));
    }

    private static Set<Item> createItems() {
        Set<Item> items = new HashSet<>();

        short[] weights = { 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2 };
        int[] values = { 10, 20, 10, 11, 12, 12, 8, 17, 11, 8, 7, 5, 5, 2, 17, 15, 14, 20, 22, 50 };

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

    public boolean isOfWeight(int weight) {
        return this.weight == (short) weight;
    }

    public short getWeight() {
        return weight;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Item item = (Item) o;
        return weight == item.weight && value == item.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight, value);
    }
}