package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvilabilityEvent implements Event <Integer>{

    private String bookTitle;

    public String getBookTitle() {
        return bookTitle;
    }

    public CheckAvilabilityEvent(String bookTitle){
        this.bookTitle = bookTitle;
    }
}
