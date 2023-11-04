import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

        ExecutorService executorService = Executors.newFixedThreadPool(10); // выбор количества пула потоков
        List<Future<Integer>> futures = new ArrayList<>(); // список Future для хранения интервалов для каждой строки

        for (String text : texts) {
            Callable<Integer> task= () -> {
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
                return maxSize;
            };
            Future<Integer> future = executorService.submit(task);
            futures.add(future);
        }
        int maxInterval = 0;

        for(Future<Integer> future: futures) {
            int interval = future.get();
            if (interval > maxInterval) {
                maxInterval = interval;
            }
        }
        executorService.shutdown(); // завершение пул потоков
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // ожидание завершеия всех потоков
        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Max Interval: " + maxInterval);      // максимальное количество интервалов
        System.out.println("Time: " + (endTs - startTs) + "ms"); // времся выполнения пула потоков
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