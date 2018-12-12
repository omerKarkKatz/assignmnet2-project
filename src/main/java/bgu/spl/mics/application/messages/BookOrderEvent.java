package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {

    private Customer customer;
    private OrderReceipt orderReceipt;

    public BookOrderEvent(Customer customer, OrderReceipt orderReceipt) {
        this.customer = customer;
        this.orderReceipt = orderReceipt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public OrderReceipt getOrderReceipt(){
        return orderReceipt;
    }

}
