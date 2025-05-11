
import os
import hashlib
from datetime import datetime
from flask import Flask, url_for,request, jsonify, request,send_file, render_template, Response, send_from_directory, abort

import vars_handler

PORT = 15555
app = Flask(__name__)
@app.route('/daily_word', methods=['GET'])
def get_daily_word():
    curr_word = vars_handler.DAILY_WORD
    return jsonify({'word':curr_word[0],'meaning':curr_word[1]})


if __name__ == '__main__':
    vars_handler.at_start()
    app.run(debug=False,port=PORT,host='0.0.0.0')