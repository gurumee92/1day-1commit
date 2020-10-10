import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


public class Solution01 {
    static class Kiosk implements Comparable<Kiosk> {
        public int number;
        public LocalDateTime operatedTime;

        public Kiosk(int number) {
            this.number = number;
            this.operatedTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0);
        }

        @Override
        public int compareTo(Kiosk other) {
            int result = operatedTime.compareTo(other.operatedTime);
            if (result == 0) {
                return Integer.compare(number, other.number);
            } else {
                return result;
            }
        }

        @Override
        public String toString() {
            return "n: " + number + " time: " + operatedTime;
        }
    }
    public int solution(int n, String[] customers) {
        PriorityQueue<Kiosk> pq = new PriorityQueue<>();
        Map<Kiosk, Integer> map = new HashMap<>();

        for (int i=1; i<=n; i++) {
            Kiosk k = new Kiosk(i);
            map.putIfAbsent(k, 0);
            pq.add(k);
        }

        for (String customer : customers) {
            String[] s = customer.split(" ");
            Kiosk k = pq.remove();

            LocalDateTime from = LocalDateTime.parse("2021/"+s[0] + " " + s[1], DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
            int time = Integer.parseInt(s[2]);
            LocalDateTime to = from.plusMinutes(time);

            if (k.operatedTime.isAfter(from)) {
                k.operatedTime = k.operatedTime.plusMinutes(time);
            } else {
                k.operatedTime = to;
            }

            map.put(k, map.get(k) + 1);
            pq.add(k);
        }

        int answer = 0;

        for (int visited : map.values()) {
            answer = Math.max(answer, visited);
        }

        return answer;
    }
}