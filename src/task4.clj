(ns task4
  (:require [clojure.string :as str]))

(defn palindrom?
  "Checks if the number n is a palindrome."
  [n]
  (let [str-n (str n)]
    (= str-n (str/reverse str-n))))

;; Java-like Recursion
(defn java-like-recur
  ([] (java-like-recur 999 99 -1))
  ([x y] (java-like-recur x y -1))
  ([x y max_p]
   (cond
     (<= x 99) max_p
     (<= y 99) (java-like-recur (dec x) x max_p)
     (palindrom? (* x y)) (java-like-recur x (dec y) (max max_p (* x y)))
     :else (java-like-recur x (dec y) max_p))))

;; Tail Recursion
(defn largest-palindrome-tail-recur
  ([] (largest-palindrome-tail-recur 999 99))
  ([x y]
   (loop [x x
          y y
          max_p -1]
     (cond
       (<= x 99) max_p
       (<= y 99) (recur (dec x) x max_p)
       (palindrom? (* x y)) (recur x (dec y) (max max_p (* x y)))
       :else (recur x (dec y) max_p)))))

;; Modular implementation

(defn generate-products []
  (for [i (range 100 1000)
        j (range 100 1000)]
    (* i j)))

(defn largest-palindrome-modular []
  (->> (generate-products)
       (filter palindrom?)
       (reduce max)))

(defn largest-palindrome-map []
  (->> (range 999 99 -1)
       (mapcat (fn [x] (map #(* x %) (range 999 99 -1))))
       (filter palindrom?)
       (apply max)))

;; Lazy implementation
(defn largest-palindrome-lazy
  ([] (largest-palindrome-lazy 1000))
  ([n]
   (->> (for [i (range 999 99 -1)
              j (range 999 99 -1)]
          (* i j))
        (filter palindrom?)
        (take n)
        (apply max))))

(comment
  (time (largest-palindrome-tail-recur)) ; "Elapsed time: 38.57525 msecs"
  (time (largest-palindrome-modular)) ; "Elapsed time: 53.140459 msecs"
  (time (largest-palindrome-map)) ; "Elapsed time: 44.700375 msecs"
  (time (largest-palindrome-lazy))) ; "Elapsed time: 46.796209 msecs"