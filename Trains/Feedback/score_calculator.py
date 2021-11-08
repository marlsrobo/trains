import csv
import glob

my_score_course = 0
max_score_course = 0
for filename in glob.glob("./**/*-scores.csv", recursive=True):
    taskname = "/".join(filename.split("/")[1:-1])
    with open(filename) as csvfile:
        reader = csv.DictReader(csvfile)
        grades = [row for row in reader]

        my_score_task = int(grades[-1]["score"])
        my_score_course += my_score_task
        max_score_task = int(grades[-1]["max"])
        max_score_course += max_score_task
        percentage = int(100 * my_score_task / max_score_task)
        print(
            f"task: {taskname}, score: {my_score_task}/{max_score_task} ({percentage}%)"
        )

percentage = int(100 * my_score_course / max_score_course)
print("============================")
print(f"total: {my_score_course}/{max_score_course} ({percentage}%)")
