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