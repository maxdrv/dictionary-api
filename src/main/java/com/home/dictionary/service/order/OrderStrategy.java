package com.home.dictionary.service.order;

import com.home.dictionary.model.phrase.Phrase;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.TreeSet;

@Component
public interface OrderStrategy {

    OrderStrategyType getType();

    Iterator<PhraseOrder> iterator(TreeSet<Phrase> phrases);

}
