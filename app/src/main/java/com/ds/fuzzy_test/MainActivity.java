package com.ds.fuzzy_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ds.fuzzy_test.fuzzy.component.MatchService;
import com.ds.fuzzy_test.fuzzy.domain.Document;
import com.ds.fuzzy_test.fuzzy.domain.Element;
import com.ds.fuzzy_test.fuzzy.domain.Match;
import com.ds.fuzzy_test.fuzzy.domain.Score;
import com.ds.fuzzy_test.fuzzy.function.PreProcessFunction;
import com.ds.fuzzy_test.fuzzy.function.ScoringFunction;
import com.ds.fuzzy_test.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ds.fuzzy_test.fuzzy.domain.ElementType.ADDRESS;
import static com.ds.fuzzy_test.fuzzy.domain.ElementType.EMAIL;
import static com.ds.fuzzy_test.fuzzy.domain.ElementType.NAME2;
import static com.ds.fuzzy_test.fuzzy.domain.ElementType.PHONE;
import static com.ds.fuzzy_test.fuzzy.domain.ElementType.TEXT;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> adr = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillNames();
        fillAdr();
        List<Document> list = getList();

        MatchService matchService = new MatchService();
//        Map<Document, List<Match<Document>>> result = matchService.applyMatch(getSearchDocument("200", "aaa"), list);
        Map<String, List<Match<Document>>> result = matchService.applyMatchByDocId(getSearchDocument("200", "Viktor"), list);


    }

    //sep ##########################################################################################

    private List<Document> getList() {
        List<Document> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add(getDocument(String.valueOf(i), names.get(i), adr.get(i)));
        }
        return result;
    }

    //sep ##########################################################################################

    private void fillNames(){
        names.add("DmitriyFedorovich");
        names.add("OlegViktorovich");
        names.add("MikhailNikolaevich");
        names.add("RomanIgorevich");
        names.add("Ruslan Anatolievich");
        names.add("Viktor Alexandrovich");
        names.add("Vladislav Maximovich");
        names.add("Stepan Olegovich");
        names.add("Sergey Ivanovich");
        names.add("Sergy Ivanich");
    }

    private void fillAdr(){
        adr.add("aaa");
        adr.add("bbb");
        adr.add("ccc");
        adr.add("ddd");
        adr.add("eee");
        adr.add("fff");
        adr.add("ggg");
        adr.add("hhh");
        adr.add("iii");
        adr.add("ggg");
    }

    //sep ##########################################################################################

    private Document getDocument(String id, String name, String adr){

        return new Document.Builder(id).setThreshold(0.01)
                .addElement(new Element.Builder().setType(EMAIL).setValue(name).createElement())
//                .addElement(new Element.Builder().setType(EMAIL).setValue(adr).createElement())
                .createDocument();
    }

    //sep ##########################################################################################

    private Document getSearchDocument(String id, String name){

        return new Document.Builder(id).setThreshold(0.001)
                .addElement(new Element.Builder().setThreshold(0.001)
                        .setType(EMAIL).setValue(name).createElement())
                .createDocument();
    }

    //sep ##########################################################################################


}


