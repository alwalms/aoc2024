(ns aoc2024.clojure.day3
  (:require [clojure.string :as string]))

(def input (slurp "resources/2024_3"))

(defn get-all-multipliers [string]
  (->> string
       (re-seq #"mul\((\d+),(\d+)\)")))

(defn multipliers-to-nums [multipliers]
  (->> multipliers
       (map (fn [row] [(Integer/parseInt (nth row 1))
                       (Integer/parseInt (nth row 2))]))))

(defmulti process (fn [part] part))

(defmethod process :part1 [_]
  (->> input
       get-all-multipliers
       multipliers-to-nums
       (map #(apply * %))
       (reduce +)))

(defmethod process :part2 [_]
  (->> (string/split input #"do\(\)")
       (map (fn [string] (string/replace string #"(?s)don't\(\).*" "")))
       (string/join #"\n")
       get-all-multipliers
       multipliers-to-nums
       (map #(apply * %))
       (reduce +)))

(comment
  (process :part1)
  (process :part2))
