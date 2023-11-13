import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Main implements Runnable {
    private static final String letters = "RLRFR";
    private static final int length = 100;
    public static final Map<Integer, Integer> sizeToFreq = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        int threads = 1000;

        for (int i = 0; i < threads; i++) {
            new Thread(new Main()).start();
        }

        // Ждем завершения всех потоков
        Thread.sleep(threads);

        // Вывод результатов
        int maxFreq = 0;
        System.out.println("Самое частое количество повторений:");
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            maxFreq = Math.max(maxFreq, entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getValue() == maxFreq) {
                System.out.println(entry.getKey() + " (встретилось " + maxFreq + " раз)");
            }
        }

        System.out.println("Другие размеры:");
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getValue() != maxFreq) {
                System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)");
            }
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    @Override
    public void run() {
        String route = generateRoute(letters, length);
        updateSizeToFreq(route);
    }

    public void updateSizeToFreq(String route) {
        int currentSequence = 0;
        for (int i = 0; i < route.length(); i++) {
            if (route.charAt(i) == 'R') {
                currentSequence++;
            } else if (currentSequence > 0) {
                synchronized (sizeToFreq) {
                    sizeToFreq.put(currentSequence, sizeToFreq.getOrDefault(currentSequence, 0) + 1);
                }
                currentSequence = 0;
            }
        }
        if (currentSequence > 0) {
            synchronized (sizeToFreq) {
                sizeToFreq.put(currentSequence, sizeToFreq.getOrDefault(currentSequence, 0) + 1);
            }
        }
    }
}
