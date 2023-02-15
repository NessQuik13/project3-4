import tkinter as tk
from tkinter import *
from tkinter import font as tkfont
from serial import *

#start up the gui
root = Tk()
bgImg = PhotoImage(file="kutte_met_rutte_small.ppm")
container = tk.Canvas(root, width=1920, height=1080)
container.pack(side="top", fill="both", expand=True)
container.create_image(0,0, image=bgImg, anchor="nw")

#languagepage


#
root.attributes("-fullscreen", True)
root.mainloop()
