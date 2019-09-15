from flask import request
from flask import send_file
from selenium import webdriver
import json, base64
import sys
import os
import re
import tempfile
from pybars import Compiler
import PyPDF2

from flask import Flask, Blueprint
from threading import Lock

app = Flask(__name__)

compiler = Compiler()
chrome_options = webdriver.ChromeOptions() 
chrome_options.add_argument('headless')
chrome_options.add_argument('--no-sandbox')
chrome_options.add_argument("--window-size=640,1080")

driver = webdriver.Chrome(chrome_options=chrome_options) 
mutex = Lock()

def send_devtools(driver, cmd, params={}):
  resource = "/session/%s/chromium/send_command_and_get_result" % driver.session_id
  url = driver.command_executor._url + resource
  body = json.dumps({'cmd': cmd, 'params': params})
  response = driver.command_executor._request('POST', url, body)
  if response['status']:
    raise Exception(response.get('value'))
  return response.get('value')

def save_as_pdf(driver, path, options={}):    
  result = send_devtools(driver, "Page.printToPDF", options)
  with open(path, 'wb') as file:
    file.write(base64.b64decode(result['data']))

def download(target_path, template_file, context):
    global driver
    global mutex
    mutex.acquire()
    data = ''
    with open(template_file, 'r', encoding="utf-8") as f:
        data=f.read()
    template = compiler.compile(data)
    f = tempfile.NamedTemporaryFile(mode='w',delete=True, encoding='utf-8', suffix='.html')
    f.write(template(context))
    driver.get("file:///" + f.name)
    f.close()
    save_as_pdf(driver, target_path)
    mutex.release()

def render_template(template, context, name='document.pdf'):
    f = tempfile.NamedTemporaryFile(mode='w',delete=True, encoding='utf-8')
    download(f.name, template, context)
    res = send_file(f.name, attachment_filename=name)
    f.close()
    return res

@app.route('/kudir', methods = ['POST'])
def kudir():
    context = request.form
    template = '/app/templates/kudir.html'
    return render_template(template, context, 'КУДиР.pdf')

@app.route('/declaration', methods = ['POST', 'GET'])
def declaration():
    data = request.form
    lengths = {
      'inn' : 12,
      'kpp' : 9,
      'fio_f' : 40,
      'fio_i' : 40,
      'fio_o' : 40,
      'mob_n' : 20,
      'n_p_k' : 2,
      'o_year' : 4,
      'nal_co' : 4,
      'resp_id' : 1,
      'r_fio_f' : 20,
      'r_fio_f' : 20,
      'r_fio_o' : 20,
      'rsp' : 160,
      'day' : 2,
      'month' : 2,
      'year' : 4,
    }
    context = {}
    for key in lengths:
      value = data[key]
      for i in range(lengths[key]):
        if key != 'rsp':
          new_key = key + '_'+ str(i + 1)
        else:
          group = i // 20
          j = i % 20
          new_key = key + '_' + str(group + 1) + '_' + str(j + 1)
        try:
          s = data[key][i]
        except IndexError:
          s = '-'
        context[new_key] = s
    template = '/app/templates/declaration.html'
    return render_template(template, context, 'Декларация.pdf')

if __name__ == "__main__":
    app.run(debug=True, host='html2pdf')