import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {
    Map<String, List<PageEntry>> entriesForEachWord = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        File[] dirListing = pdfsDir.listFiles();

        if (dirListing != null) {

            for (File file : dirListing) {
                PdfDocument pdf = new PdfDocument(new PdfReader(file));

                int numberOfPages = pdf.getNumberOfPages();

                for (int i = 1; i <= numberOfPages; i++) {

                    PdfPage page = pdf.getPage(i);
                    String text = PdfTextExtractor.getTextFromPage(page);
                    String[] words = text.split("\\P{IsAlphabetic}+");

                    Map<String, Integer> frequency = new HashMap<>();
                    for (String word : words) {
                        if (word.isEmpty()) {
                            continue;
                        }
                        frequency.put(word.toLowerCase(), frequency.getOrDefault(word.toLowerCase(), 0) + 1);
                    }

                    for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
                        String word = entry.getKey();
                        int count = entry.getValue();

                        PageEntry pageEntry = new PageEntry(file.getName(), i, count);

                        if (entriesForEachWord.containsKey(word)) {

                            List<PageEntry> entries = entriesForEachWord.get(word);
                            entries.add(pageEntry);
                            entriesForEachWord.replace(word, entries);

                        } else {

                            List<PageEntry> newEntries = new ArrayList<>();
                            newEntries.add(pageEntry);
                            entriesForEachWord.put(word, newEntries);

                        }
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> pageEntries = entriesForEachWord.get(word);

        return pageEntries.stream()
                .sorted(PageEntry::compareTo)
                .collect(Collectors.toList());
    }
}
