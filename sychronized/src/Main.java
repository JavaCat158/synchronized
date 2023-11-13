import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        long startTs = System.currentTimeMillis(); // start time
        final ExecutorService threadPoll = Executors.newFixedThreadPool(4);
        List<Future<Integer>> list = new ArrayList<>();

        for (String text : texts) {
            Callable<Integer> myCallable = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };
            list.add(threadPoll.submit(myCallable));
        }
        int resultMax = 0;
        for (Future<Integer> fut : list) {
            try {
                if (resultMax < fut.get()) {
                    resultMax = fut.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                return;
            }
        }
        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + " ms");
        System.out.println("максимальный интервал значений среди всех строк:  " + resultMax);
        threadPoll.shutdown();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}