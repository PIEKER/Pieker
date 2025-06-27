SELECT * FROM input_data;

SELECT * FROM risk_data;

SELECT r.id, r.content, d.risk FROM input_data r LEFT JOIN risk_data d ON r.id = d.id;