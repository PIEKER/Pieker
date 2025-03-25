SELECT c.customer_id, c.customer_name, o.order_id, p.product_name, s.shipping_date, s.shipping_status
FROM customers c
JOIN orders o ON c.customer_id = o.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p ON oi.product_id = p.product_id
JOIN shipments s ON o.order_id = s.order_id
WHERE c.customer_status = 'active' AND s.shipping_status != 'delayed' AND o.order_date >= '2024-01-01';

SELECT e.employee_id, e.employee_name, e.salary, d.department_name, m.manager_name
FROM employees e
JOIN departments d ON e.department_id = d.department_id
JOIN managers m ON e.manager_id = m.manager_id
WHERE e.salary > (SELECT AVG(salary) FROM employees WHERE department_id = e.department_id) AND e.hire_date < '2020-01-01';

SELECT su.supplier_name, c.category_name, pr.product_name, r.review_date, r.rating
FROM suppliers su
JOIN products pr ON su.supplier_id = pr.supplier_id
JOIN categories c ON pr.category_id = c.category_id
JOIN reviews r ON pr.product_id = r.product_id
WHERE c.category_name = 'Electronics' AND r.review_date >= '2023-06-01' AND r.rating >= 4
ORDER BY r.review_date DESC;

SELECT pr.project_name, e.employee_name, a.assignment_date, t.task_name, t.due_date
FROM projects pr
JOIN assignments a ON pr.project_id = a.project_id
JOIN employees e ON a.employee_id = e.employee_id
JOIN tasks t ON pr.project_id = t.project_id
LEFT JOIN task_status ts ON t.task_id = ts.task_id
WHERE pr.start_date >= '2023-01-01' AND ts.status NOT IN ('Completed', 'Cancelled')
  AND EXISTS (SELECT 1 FROM budgets b WHERE b.project_id = pr.project_id AND b.budget_amount > 100000);
