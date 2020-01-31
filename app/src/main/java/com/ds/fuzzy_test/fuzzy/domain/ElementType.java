package com.ds.fuzzy_test.fuzzy.domain;


import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.ds.fuzzy_test.fuzzy.function.MatchOptimizerFunction.dateSortOptimizer;
import static com.ds.fuzzy_test.fuzzy.function.MatchOptimizerFunction.numberSortOptimizer;
import static com.ds.fuzzy_test.fuzzy.function.MatchOptimizerFunction.searchGroupOptimizer;
import static com.ds.fuzzy_test.fuzzy.function.PreProcessFunction.addressPreprocessing;
import static com.ds.fuzzy_test.fuzzy.function.PreProcessFunction.namePreprocessing;
import static com.ds.fuzzy_test.fuzzy.function.PreProcessFunction.none;
import static com.ds.fuzzy_test.fuzzy.function.PreProcessFunction.numberPreprocessing;
import static com.ds.fuzzy_test.fuzzy.function.PreProcessFunction.removeDomain;
import static com.ds.fuzzy_test.fuzzy.function.PreProcessFunction.removeSpecialChars;
import static com.ds.fuzzy_test.fuzzy.function.PreProcessFunction.usPhoneNormalization;
import static com.ds.fuzzy_test.fuzzy.function.SimilarityMatchFunction.dateDifferenceWithinYear;
import static com.ds.fuzzy_test.fuzzy.function.SimilarityMatchFunction.equality;
import static com.ds.fuzzy_test.fuzzy.function.SimilarityMatchFunction.numberDifferenceRate;
import static com.ds.fuzzy_test.fuzzy.function.SimilarityMatchFunction.soundex;
import static com.ds.fuzzy_test.fuzzy.function.TokenizerFunction.decaGramTokenizer;
import static com.ds.fuzzy_test.fuzzy.function.TokenizerFunction.triGramTokenizer;
import static com.ds.fuzzy_test.fuzzy.function.TokenizerFunction.valueTokenizer;
import static com.ds.fuzzy_test.fuzzy.function.TokenizerFunction.wordTokenizer;

/**
 *
 * Enum to define different types of Element.
 * This is used only to categorize the data, and apply functions at different stages of match.
 * The functions, can be overridden from Element class using the appropriate setters at the time of creation.
 */
public enum ElementType {
    NAME(namePreprocessing(), wordTokenizer(), soundex(), searchGroupOptimizer()),
    TEXT(removeSpecialChars(), wordTokenizer(), soundex(), searchGroupOptimizer()),
    ADDRESS(addressPreprocessing(), wordTokenizer(), soundex(), searchGroupOptimizer()),
    EMAIL(removeDomain(), triGramTokenizer(),  equality(), searchGroupOptimizer()),
    PHONE(usPhoneNormalization(),decaGramTokenizer(), equality(), searchGroupOptimizer()),
    NUMBER(numberPreprocessing(),valueTokenizer(),numberDifferenceRate(), numberSortOptimizer()),
    DATE(none(),valueTokenizer(),dateDifferenceWithinYear(), dateSortOptimizer()),

    NAME2(namePreprocessing(), triGramTokenizer(), equality(), searchGroupOptimizer());




    private final Function<Object, Object> preProcessFunction;

    private final Function<Element, Stream<Token>> tokenizerFunction;

    private final BiFunction<Token, Token, Double> similarityMatchFunction;

    private final Function<List<Token>, Stream<Match<Token>>> matchOptimizerFunction;

    ElementType(Function<Object, Object> preProcessFunction, Function<Element, Stream<Token>> tokenizerFunction,
                BiFunction<Token, Token, Double> similarityMatchFunction, Function<List<Token>, Stream<Match<Token>>> matchOptimizerFunction) {
        this.preProcessFunction = preProcessFunction;
        this.tokenizerFunction = tokenizerFunction;
        this.similarityMatchFunction = similarityMatchFunction;
        this.matchOptimizerFunction = matchOptimizerFunction;
    }

    public Function<Object, Object> getPreProcessFunction() {
        return preProcessFunction;
    }


    public Function<Element, Stream<Token>> getTokenizerFunction() {
        return tokenizerFunction;
    }

    public BiFunction<Token, Token, Double> getSimilarityMatchFunction() {
        return similarityMatchFunction;
    }

    public Function<List<Token>, Stream<Match<Token>>> getMatchOptimizerFunction() {
        return matchOptimizerFunction;
    }
}
