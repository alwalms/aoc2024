(ns aoc2024.clojure.day1
  (:require [clojure.string :as string]))

(def input (string/split-lines (slurp "resources/2024_1")))

(def parsed-input (->> input
                       (map (fn [line] (string/split line #"\s+")))))

(def left-nums (->> (map first parsed-input)
                    (map #(Integer/parseInt %))))

(def right-nums (->> (map second parsed-input)
                     (map #(Integer/parseInt %))))

(defn calc-similarity [num]
  (let [right-matches (filter #(= num %) right-nums)]
    (-> right-matches
        count
        (* num))))

(defmulti process (fn [part] part))

(defmethod process :part1 [_]
  (let [left-nums-ordered (sort left-nums)
        right-nums-ordered (sort right-nums)]
    (->> (map - left-nums-ordered right-nums-ordered)
         (map abs)
         (reduce +)))
  )

(defmethod process :part2 [_]
  (->> left-nums
       (map (fn [x] (calc-similarity x)))
       (reduce +)))

(comment
  (process :part1)
  (process :part2)
  )
