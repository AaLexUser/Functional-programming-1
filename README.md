# Лабораторная работа #1

## Дисциплина

Функциональное программирование

## Выполнил

Лапин Алексей Александрович, P34102

## Цель работы

Освоить базовые приёмы и абстракции функционального программирования: функции, поток управления и поток данных, сопоставление с образцом, рекурсия, свёртка, отображение, работа с функциями как с данными, списки.

## Вариант

| Задания | [4](https://projecteuler.net/problem=4), [27](https://projecteuler.net/problem=27) |
|---------|------|

## Требования

Для каждой проблемы должно быть представлено несколько решений:

1. монолитные реализации с использованием:
   - хвостовой рекурсии;
   - рекурсии (вариант с хвостовой рекурсией не является примером рекурсии);
2. модульной реализации, где явно разделена генерация последовательности, фильтрация и свёртка (должны использоваться функции reduce/fold, filter и аналогичные);
3. генерация последовательности при помощи отображения (map);
4. работа со спец. синтаксисом для циклов (где применимо);
5. работа с бесконечными списками для языков, поддерживающих ленивые коллекции или итераторы как часть языка (к примеру Haskell, Clojure);
6. реализация на любом удобном для вас традиционном языке программирования для сравнения.

## Условия задач

**Задача 4. Largest Palindrome Product**

A palindromic number reads the same both ways. The largest palindrome made from the product of two $2$-digit numbers is $9009 = 91 \times 99$.

Find the largest palindrome made from the product of two $3$-digit numbers.

**Задача 27. Quadratic Primes**

Euler discovered the remarkable quadratic formula:

$$n^2 + n + 41$$

It turns out that the formula will produce $40$ primes for the consecutive integer values $0 \le n \le 39$. However, when $n = 40, 40^2 + 40 + 41 = 40(40 + 1) + 41$ is divisible by $41$, and certainly when $n = 41, 41^2 + 41 + 41$ is clearly divisible by $41$.

The incredible formula $n^2 - 79n + 1601$ was discovered, which produces $80$ primes for the consecutive values $0 \le n \le 79$. The product of the coefficients, $-79$ and $1601$, is $-126479$.

Considering quadratics of the form:

$n^2 + an + b$, where $|a| < 1000$ and $|b| \le 1000$

where $|n|$ is the modulus/absolute value of $n$

e.g. $|11| = 11$ and $|-4| = 4$

Find the product of the coefficients, $a$ and $b$, for the quadratic expression that produces the maximum number of primes for consecutive values of $n$, starting with $n = 0$.

## Ход работы

**Настройка проекта**

[`deps.edn`](./deps.edn):

```clojure
{:paths ["src"]
 :deps {org.clojure/clojure {:mvn/version "1.12.0"}}
 :aliases {:test {:extra-paths ["test"]
                  :extra-deps  {lambdaisland/kaocha {:mvn/version "1.91.1392"}}
                  :main-opts   ["-m" "kaocha.runner"]}}}
```

GitHub Actions для Clojure:

[`clojure.yml`](./.github/workflows/clojure.yml):

```yaml
name: Clojure workflow

on: [push]

jobs:

  clojure:

    strategy:
      matrix:
        os: [ubuntu-latest]

    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v3

      - name: Prepare java
        uses: actions/setup-java@v3
        with:
          distribution: 'zulu'
          java-version: '8'

      - name: Install clojure tools
        uses: DeLaGuardo/setup-clojure@12.1
        with:
          cli: 1.10.1.693             
          clj-kondo: 2022.05.31       
          cljfmt: 0.10.2              

      - name: clj-fmt fix
        run: cljfmt fix src test

      - name: clj-kondo checks
        run: clj-kondo --lint src test

      - name: run-tests
        run: clojure -Mtest
```

## Выполнения задания №4:

### Clojure

[`task4.clj`](./src/task4.clj):

```clojure
(ns task4
  (:require [clojure.string :as str]))

(defn palindrom?
  "Checks if the number n is a palindrome."
  [n]
  (let [str-n (str n)]
    (= str-n (str/reverse str-n))))

;; 1.1 Java-like Recursion
(defn java-like-recur
  ([] (java-like-recur 999 99 -1))
  ([x y] (java-like-recur x y -1))
  ([x y max_p]
   (cond
     (<= x 99) max_p
     (<= y 99) (java-like-recur (dec x) x max_p)
     (palindrom? (* x y)) (java-like-recur x (dec y) (max max_p (* x y)))
     :else (java-like-recur x (dec y) max_p))))

;; 1.2 Tail Recursion
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

;; 2. Modular implementation

(defn generate-products []
  (for [i (range 100 1000)
        j (range 100 1000)]
    (* i j)))

(defn largest-palindrome-modular []
  (->> (generate-products)
       (filter palindrom?)
       (reduce max)))

;; 3. Sequence generation using map
(defn largest-palindrome-map []
  (->> (range 999 99 -1)
       (mapcat (fn [x] (map #(* x %) (range 999 99 -1))))
       (filter palindrom?)
       (apply max)))

;; 4. Special syntax, atoms
(defn largest-palindrome-atom []
  (let [max-p (atom -1)]
    (doseq [i (range 999 99 -1)
            j (range 999 99 -1)]
      (let [p (* i j)]
        (when (palindrom? p)
          (swap! max-p max p))))
    @max-p))

;; 5. Lazy implementation
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
  (time (largest-palindrome-atom)) ; "Elapsed time: 98.430583 msecs"
  (time (largest-palindrome-lazy))) ; "Elapsed time: 46.796209 msecs"
```

### Тесты

[`task4_test.clj`](./test/task4_test.clj):

```clojure
(ns task4-test
  (:require [task4 :refer [palindrom?
                           java-like-recur
                           largest-palindrome-tail-recur
                           largest-palindrome-modular largest-palindrome-map
                           largest-palindrome-lazy]]
            [clojure.test :refer [deftest is testing are run-tests]]))

(deftest test-palindrom?
  (testing "Palindrome number identification"
    (are [x y] (= (palindrom? x) y)
      9009 true
      12321 true
      12345 false
      1 true
      11 true
      10 false)))

(deftest name-test
  (testing "Java Recursion StackOverflowError"
    (is (thrown? StackOverflowError (java-like-recur)))))

(deftest test-all-implementations-equal
  (testing "All implementations should return the same result"
    (let [expected 906609]
      (are [f] (= (f) expected)
        largest-palindrome-tail-recur
        largest-palindrome-modular
        largest-palindrome-map
        largest-palindrome-lazy))))

(run-tests)
```

### Python

```python
def is_palindrome(n):
    return str(n) == str(n)[::-1]


def largest_palindrome_product():
    max_palindrome = 0
    for i in range(999, 99, -1):
        for j in range(999, 99, -1):
            product = i * j
            if product > max_palindrome and is_palindrome(product):
                max_palindrome = product
    return max_palindrome


if __name__ == "__main__":
    print(f"Largest palindrome: {largest_palindrome_product()}")
```

## Выполнения задания №27:

### Clojure

[`task27.clj`](./src/task27.clj):

```clojure
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

;; 5. Special syntax, atoms
(defn find-max-quadratic-special []
  (let [result (atom [0 0 0])]
    (doseq [a (range -999 1000)
            b (range -1000 1001)]
      (let [count (count-consecutive-primes a b)]
        (when (> count (last @result))
          (reset! result [a b count]))))
    (let [[a b count] @result]
      {:product (* a b) :a a :b b :count count})))

;; 6. Lazy collections
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
```

### Тесты

[`task27_test.clj`](./test/task27_test.clj):

```clojure
(ns task27-test
  (:require [clojure.test :refer [deftest testing are run-tests]]
            [task27 :refer [prime? quadratic count-consecutive-primes
                            find-max-quadratic-tail-rec find-max-quadratic-rec
                            find-max-quadratic-modular find-max-quadratic-map
                            find-max-quadratic-loop find-max-quadratic-special
                            find-max-quadratic-lazy]]))

(deftest test-prime?
  (testing "Prime number identification"
    (are [x y] (= (prime? x) y)
      2 true
      3 true
      4 false
      5 true
      1 false
      0 false
      -1 false)))

(deftest test-quadratic
  (testing "Quadratic function calculation"
    (are [a b c result] (= (quadratic a b c) result)
      1 1 1 3
      1 1 2 7
      0 0 0 0
      -1 -1 -1 1)))

(deftest test-count-consecutive-primes
  (testing "Counting consecutive primes"
    (are [a b count] (= (count-consecutive-primes a b) count)
      1 41 40
      -79 1601 80
      0 0 0)))

(def expected-answer {:product -59231 :a -61 :b 971 :count 71})

(defn- validate-quadratic-result [result]
  (and (map? result)
       (integer? (:a result))
       (integer? (:b result))
       (integer? (:count result))))

(deftest test-find-max-quadratic-implementations
  (testing "Various implementations of find-max-quadratic"
    (are [f] (validate-quadratic-result (f))
      find-max-quadratic-tail-rec
      find-max-quadratic-rec
      find-max-quadratic-modular
      find-max-quadratic-map
      find-max-quadratic-loop
      find-max-quadratic-special
      find-max-quadratic-lazy)))

(deftest test-find-max-quadratic-correctness
  (testing "Correctness of find-max-quadratic implementations"
    (are [f] (= (f) expected-answer)
      find-max-quadratic-tail-rec
      find-max-quadratic-rec
      find-max-quadratic-modular
      find-max-quadratic-map
      find-max-quadratic-loop
      find-max-quadratic-special
      find-max-quadratic-lazy)))

(run-tests)
```

### Python

```python
from functools import lru_cache


@lru_cache(maxsize=None)
def is_prime(n):
    if n < 2:
        return False
    for i in range(2, int(n**0.5) + 1):
        if n % i == 0:
            return False
    return True


def count_consecutive_primes(a, b):
    n = 0
    while is_prime(n*n + a*n + b):
        n += 1
    return n


def find_max_quadratic():
    max_count = 0
    max_coeffs = (0, 0)

    for a in range(-999, 1000):
        for b in range(-1000, 1001):
            count = count_consecutive_primes(a, b)
            if count > max_count:
                max_count = count
                max_coeffs = (a, b)

    return max_coeffs


if __name__ == "__main__":
    a, b = find_max_quadratic()
    print(f"Product of coefficients: {a*b}")
    print(f"Coefficients: {a}, {b}")
    print(f"Consecutive primes: {count_consecutive_primes(a, b)}")
```

## Вывод

Работа над задачами из Project Euler позволила мне глубже понять и освоить основные концепции функционального программирования. Использование различных подходов, таких как хвостовая рекурсия, модульная реализация, отображение и работа с ленивыми коллекциями позволило глубже понять особенности и преимущества функционального стиля.

Применение хвостовой рекурсии и ленивых коллекций позволило эффективно работать с большими объемами данных, избегая переполнения стека и излишнего потребления памяти.

Использование функций высшего порядка (map, filter, reduce) значительно упростило обработку последовательностей данных, делая код более лаконичным и выразительным.

Особенно впечатлило удобство выполнения кода в REPL прямо из редактора. Приятно видеть результаты выполнения функции сразу же после её написания.
