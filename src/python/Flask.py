from KeyYearMonthSearch import *
from flask import Flask, jsonify, request

app = Flask(__name__)

@app.route('/search', methods=['GET'])
def search():
    file_path = 'src\\main\\resources\\data.csv'
    key_documents, rows = read_csv(file_path)
    searchkey = request.args.get('searchkey')
    year = request.args.get('year')
    month = request.args.get('month')
    result = key_year_month_search(searchkey, year, month, key_documents, rows)
    return result
if __name__ == '__main__':
    app.run(debug=True)
