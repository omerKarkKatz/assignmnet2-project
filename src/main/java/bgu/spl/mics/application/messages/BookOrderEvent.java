package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {

    private Customer customer;
    private String bookTitle;
    private OrderReceipt orderReceipt;

    public BookOrderEvent(Customer customer, String bookTitle, OrderReceipt orderReceipt) {
        this.customer = customer;
        this.orderReceipt = orderReceipt;
        this.bookTitle = bookTitle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public OrderReceipt getOrderReceipt(){
        return orderReceipt;
    }

    public String getBookTitle() { return bookTitle; }


}
