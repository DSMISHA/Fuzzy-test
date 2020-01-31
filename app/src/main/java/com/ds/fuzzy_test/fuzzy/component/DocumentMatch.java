package com.ds.fuzzy_test.fuzzy.component;


import com.ds.fuzzy_test.fuzzy.domain.Document;
import com.ds.fuzzy_test.fuzzy.domain.Element;
import com.ds.fuzzy_test.fuzzy.domain.ElementClassification;
import com.ds.fuzzy_test.fuzzy.domain.Match;
import com.ds.fuzzy_test.fuzzy.domain.Score;


import org.apache.commons.lang3.BooleanUtils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * <p>
 * Starts the Matching process by element level matching and aggregates the results back
 * This uses the ScoringFunction defined at each Document to get the aggregated Document score for matched Elements
 */
public class DocumentMatch {

    private static ElementMatch elementMatch = new ElementMatch();

    /**
     * Executes matching of a document stream
     *
     * @param documents Stream of Document objects
     * @return Stream of Match of Document type objects
     */
    public Stream<Match<Document>> matchDocuments(Stream<Document> documents) {
        Stream<Element> elements = documents.flatMap(d -> d.getPreProcessedElement().stream());
        Map<ElementClassification, List<Element>> elementMap = elements.collect(Collectors.groupingBy(Element::getElementClassification));

        List<Match<Element>> matchedElements = new ArrayList<>();
        elementMap.forEach(new BiConsumer<ElementClassification, List<Element>>() {
            @Override
            public void accept(ElementClassification key, List<Element> value) {
                List<Match<Element>> result = elementMatch.matchElements(key, value.parallelStream()).collect(Collectors.toList());
                matchedElements.addAll(result);
            }
        });

        return rollupDocumentScore(matchedElements.parallelStream());
    }

    private Stream<Match<Document>> rollupDocumentScore(Stream<Match<Element>> matchElementStream) {

        Map<Document, Map<Document, List<Match<Element>>>> groupBy = matchElementStream
                .collect(Collectors.groupingBy(matchElement -> matchElement.getData().getDocument(),
                        Collectors.groupingBy(matchElement -> matchElement.getMatchedWith().getDocument())));

        return groupBy.entrySet().parallelStream().flatMap(leftDocumentEntry -> leftDocumentEntry.getValue().entrySet()
                .parallelStream()
                .flatMap(rightDocumentEntry -> {
                    List<Score> childScoreList = rightDocumentEntry.getValue()
                            .stream()
                            .map(Match::getScore)
                            .collect(Collectors.toList());
                    Match<Document> leftMatch = new Match<Document>(leftDocumentEntry.getKey(), rightDocumentEntry.getKey(), childScoreList);
                    if (BooleanUtils.isNotFalse(rightDocumentEntry.getKey().isSource())) {
                        Match<Document> rightMatch = new Match<Document>(rightDocumentEntry.getKey(), leftDocumentEntry.getKey(), childScoreList);
                        return Stream.of(leftMatch, rightMatch);
                    }
                    return Stream.of(leftMatch);
                }))
                .filter(new Predicate<Match<Document>>() {
                    @Override
                    public boolean test(Match<Document> match) {
                        return match.getResult() > match.getData().getThreshold();
                    }
                });



    }
}
