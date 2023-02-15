import tkinter as tk
from tkinter import *

# possible code to lift canvas?
# Since Canvas has override the .tkraise() function, you need to call TCL command directly:
# self.canvas.tk.call('raise', self.canvas._w)
root = Tk()
#root.attributes("-fullscreen", True)
root.geometry("1920x1080")
bg_img = tk.PhotoImage(file="Images\kutte_met_rutte_small.png")
bg_eng = tk.PhotoImage(file="Images\welkom_engels.png")
bg_dutch = tk.PhotoImage(file="Images\welkom_nederlands.png")
eng_button_img = tk.PhotoImage(file="Images\English.png")
dutch_button_img = tk.PhotoImage(file="Images\Dutch.png")
card_img = tk.PhotoImage(file="Images\insert-card.png")


select_lang = "dutch"
def load_frame_start():
    clear_widgets(frame_inscard)
    frame_start.tkraise()
    start_canvas = Canvas(frame_start, width=1920, height=1080)
    start_canvas.pack(fill="both", expand=True)
    start_canvas.create_image(0,0, image=bg_img, anchor="nw")

    start_canvas.create_text(960,400, text="Kies een taal:", font=("TkMenuFont", 26))
    button_eng = Button(start_canvas, image=eng_button_img, command=lambda:eng_clicked())
    button_dutch = Button(start_canvas, image=dutch_button_img, command=lambda:dutch_clicked())

    button_eng_win = start_canvas.create_window(600,450, anchor="nw", window=button_eng)
    button_dutch_win = start_canvas.create_window(1200,450, anchor="nw", window=button_dutch)

def load_frame_inscard():
    clear_widgets(frame_start)
    frame_inscard.tkraise()
    inscard_canvas = Canvas(frame_inscard, width=1920, height=1080)
    inscard_canvas.pack(fill="both", expand=True)
    if select_lang == "dutch":
        inscard_canvas.create_image(0,0, image=bg_dutch, anchor="nw")
    else:
        inscard_canvas.create_image(0,0, image=bg_eng, anchor="nw")


    card_label = Label(inscard_canvas, image=card_img).place(x= 832, y=322)

    print("inscard")


def eng_clicked():
    global select_lang
    select_lang = "english"
    load_frame_inscard()

def dutch_clicked():
    global select_lang
    select_lang = "dutch"
    load_frame_inscard()

def clear_widgets(frame):
    for widget in frame.winfo_children():
        widget.destroy()

frame_start = tk.Frame(root, width=1920,height=1080)
frame_inscard = tk.Frame(root, width=1920,height=1080)

for frame in (frame_start, frame_inscard):
    frame.grid(row=0,column=0)

load_frame_start()
root.mainloop()






# class Gui:
#     def __init__(self, master):
#         myFrame = Frame(master)
#         myFrame.pack()

#         self.button = Button(master, text="test", command=self.clicker)
#         self.button.pack(pady=20)

#     def clicker(self):
#         print("test")

# gui = Gui(root)
