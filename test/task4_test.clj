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