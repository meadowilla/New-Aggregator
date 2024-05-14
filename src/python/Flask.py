from KeyYearMonthSearch import *
import Indexing
from flask import Flask, jsonify, request

app = Flask(__name__)

@app.route('/search', methods=['GET'])
def search():
    searchkey = request.args.get('searchkey')
    year = request.args.get('year')
    month = request.args.get('month')
    result = key_year_month_search(searchkey, year, month)
    return result
if __name__ == '__main__':
    app.run(debug=True)
