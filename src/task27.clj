(ns task27)

(defn _prime? [n]
  (cond
    (< n 2) false
    (= n 2) true
    :else
    (not-any? #(zero? (mod n %))
              (range 2 (inc (Math/sqrt n))))))
(def prime? (memoize _prime?))

(defn quadratic [a b n]
  (+ (* n n) (* a n) b))

(defn count-consecutive-primes
  [a b]
  (count (take-while prime? (map #(quadratic a b %) (range)))))


;; 1. Tail recursion
(defn find-max-quadratic-tail-rec
  ([] (find-max-quadratic-tail-rec -999 1000 -1000 1001))
  ([a_lower a_upper b_lower b_upper]
   (find-max-quadratic-tail-rec a_upper b_upper a_lower b_lower 0 [0 0]))
  ([a_upper b_upper a b max-count max-coeffs]
   (cond
     (> a a_upper) {:product (apply * max-coeffs) :a (first max-coeffs) :b (second max-coeffs) :count max-count}
     (> b b_upper) (recur a_upper b_upper (inc a) -1000 max-count max-coeffs)
     :else (let [count (count-consecutive-primes a b)]
             (cond
               (> count max-count) (recur a_upper b_upper a (inc b) count [a b])
               :else (recur a_upper b_upper a (inc b) max-count max-coeffs))))))
(find-max-quadratic-tail-rec)
;; 2. Regular recursion (non-tail)

(defn find-max-quadratic-rec
  ([] (find-max-quadratic-tail-rec -999 1000 -1000 1001))
  ([a_lower a_upper b_lower b_upper]
   (find-max-quadratic-tail-rec a_upper b_upper a_lower b_lower 0 [0 0]))
  ([a_upper b_upper a b max-count max-coeffs]
   (cond
     (> a a_upper) {:product (apply * max-coeffs) :a (first max-coeffs) :b (second max-coeffs) :count max-count}
     (> b b_upper) (find-max-quadratic-tail-rec a_upper b_upper (inc a) -1000 max-count max-coeffs)
     :else (let [count (count-consecutive-primes a b)]
             (find-max-quadratic-tail-rec a_upper b_upper a (inc b)
                                          (if (> count max-count) count max-count)
                                          (if (> count max-count) [a b] max-coeffs))))))

;; 3. Modular implementation

(def coefficients
  (for [a (range -999 1000)
        b (range -1000 1001)]
    [a b]))

(defn generate-counts []
  (map (fn [[a b]] {:product (* a b) :a a :b b :count (count-consecutive-primes a b)}) coefficients))

(defn find-max-quadratic-modular []
  (->> (generate-counts)
       (filter #(> (:count %) 0))
       (apply max-key :count)))

;; 4. Sequence generation using map
(defn find-max-quadratic-map []
  (->> (range -999 1000)
       (mapcat (fn [a]
                 (map (fn [b] {:product (* a b) :a a :b b :count (count-consecutive-primes a b)}) (range -1000 1001))))
       (filter #(> (:count %) 0))
       (apply max-key :count)))

;; 5. Special syntax for loops
(defn find-max-quadratic-loop []
  (loop [a -999, b -1000, max-count 0, max-coeffs [0 0]]
    (cond
      (> a 1000) {:product (apply * max-coeffs) :a (first max-coeffs) :b (second max-coeffs) :count max-count}
      (> b 1001) (recur (inc a) -1000 max-count max-coeffs)
      :else (let [count (count-consecutive-primes a b)]
              (recur a (inc b)
                     (if (> count max-count) count max-count)
                     (if (> count max-count) [a b] max-coeffs))))))

(defn find-max-quadratic-special []
  (let [result (atom [0 0 0])]
    (doseq [a (range -999 1000)
            b (range -1000 1001)]
      (let [count (count-consecutive-primes a b)]
        (when (> count (last @result))
          (reset! result [a b count]))))
    (let [[a b count] @result]
      {:product (* a b) :a a :b b :count count})))


(defn find-max-quadratic-lazy []
  (let [[a b count]
        (->> (for [a (range -999 1000)
                   b (range -1000 1001)]
               [a b (count-consecutive-primes a b)])
             (apply max-key last))]
    {:product (* a b) :a a :b b :count count}))


(comment
  (time (find-max-quadratic-tail-rec)) ; "Elapsed time: 723.69525 msecs"
  (time (find-max-quadratic-rec)) ; "Elapsed time: 714.869 msecs"
  (time (find-max-quadratic-loop)) ; "Elapsed time: 713.806167 msecs"
  (time (find-max-quadratic-special)) ; "Elapsed time: 993.546583 msecs"
  (time (find-max-quadratic-modular)) ; "Elapsed time: 1272.226791 msecs"
  (time (find-max-quadratic-map)) ; "Elapsed time: 826.978 msecs"
  (time (find-max-quadratic-lazy))) ; "Elapsed time: 1133.48425 msecs"