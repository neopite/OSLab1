package com.company;

public class Main {

    public static void main(String[] args) {

        Allocator allocator = new Allocator(256);
        /*
        System.out.println("0===================================");
        int index0=allocator.memAlloc(15);
        MemoryPrinter.printMemory(allocator);
        System.out.println("1===================================");
        int index1 = allocator.memAlloc(60);
        MemoryPrinter.printMemory(allocator);
        System.out.println("2===================================");
        int index2 = allocator.memAlloc(12);
        MemoryPrinter.printMemory(allocator);
        System.out.println("3===================================");
        allocator.memAlloc(12);
        MemoryPrinter.printMemory(allocator);
        System.out.println("4===================================");
        int index4 = allocator.memRealloc(index1, 100);
        MemoryPrinter.printMemory(allocator);
        System.out.println("5===================================");
       allocator.memFree(index2);
        MemoryPrinter.printMemory(allocator);
        System.out.println("6===================================");
        allocator.memFree(index0);
        MemoryPrinter.printMemory(allocator);
    */
        int index0 = allocator.memAlloc(512);
        int index1 = allocator.memAlloc(1028);
        allocator.memRealloc(index1,512);
        MemoryPrinter.printMemory(allocator);


    }
}


