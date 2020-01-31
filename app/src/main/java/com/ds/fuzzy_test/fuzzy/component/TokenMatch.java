package com.ds.fuzzy_test.fuzzy.component;

import com.ds.fuzzy_test.fuzzy.domain.ElementClassification;
import com.ds.fuzzy_test.fuzzy.domain.Match;
import com.ds.fuzzy_test.fuzzy.domain.Token;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Matches at Token level, this class uses the SimilarityMatchFunction to get a score at a Token level
 * This class also optimizes which tokens undergo match, by breaking it to NGram and figuring out the Search Groups
 */
public class TokenMatch {

    public Stream<Match<Token>> matchTokens(ElementClassification elementClassification, Stream<Token> input) {

        return elementClassification.getMatchOptimizerFunction().apply(input.collect(Collectors.toList()))
                .filter(tokenMatch -> tokenMatch.getResult() >= tokenMatch.getData().getElement().getThreshold());
    }
}
