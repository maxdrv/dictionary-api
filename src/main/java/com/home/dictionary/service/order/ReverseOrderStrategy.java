package com.home.dictionary.service.order;

import com.home.dictionary.model.phrase.Phrase;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.TreeSet;

@Component
public class ReverseOrderStrategy implements OrderStrategy {

    @Override
    public OrderStrategyType getType() {
        return OrderStrategyType.REVERSE;
    }

    @Override
    public Iterator<PhraseOrder> iterator(TreeSet<Phrase> phrases) {
        Iterator<Phrase> phraseIterator = phrases.descendingIterator();
        var current = new int[]{phrases.size() - 1};

        return new Iterator<>() {

            @Override
            public boolean hasNext() {
                return phraseIterator.hasNext();
            }

            @Override
            public PhraseOrder next() {
                Phrase phrase = phraseIterator.next();
                if (phrase == null) {
                    return null;
                }
                int toReturn = current[0];
                current[0]--;
                return new PhraseOrder(phrase, toReturn);
            }

        };
    }

}
