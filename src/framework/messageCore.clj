(ns
  ^{:author cWX205128}
  framework.messageCore
  (:import [java.io File])
  (:use [framework.properties])
  (:use [framework.messageBox])
  (:use [framework.managedResoreces])
  )

(def workerSeq (atom ()))
(let [worker_path (File. (:worker_path properties))]
  (do
    (def worker_seq (seq (.list worker_path)))
    (loop [x worker_seq]
      (if (nil? (first x)) nil
        (recur
          (do
            (def keyOfWorker (first (.split (first x) "[.]")))
            (swap! workerSeq concat [keyOfWorker])
            (eval
              (read-string
                (str "(require '[framework.worker." keyOfWorker " :as " keyOfWorker "])")))
            (rest x)))))))

(loop [x @workerSeq]
  (if (nil? (first x)) nil
    (recur
      (do
        (def rThread
          (eval
            (read-string
              (str "(Thread. (proxy [Runnable][] (run [] (" (first x) "/work \""
                (first x) "\"))) \"" (first x) "\")"))))
        (swap! rigistedThread concat [rThread])
        (box_init (first x))
        (rest x)))))