UPDATE klage.text
SET content = REPLACE(content, 'heading-one', 'h1')
WHERE created > '2023-03-24';

UPDATE klage.text
SET content = REPLACE(content, 'heading-two', 'h2')
WHERE created > '2023-03-24';

UPDATE klage.text
SET content = REPLACE(content, 'heading-three', 'h3')
WHERE created > '2023-03-24';

UPDATE klage.text
SET content = REPLACE(content, 'heading-four', 'h4')
WHERE created > '2023-03-24';

UPDATE klage.text
SET content = REPLACE(content, 'paragraph', 'p')
WHERE created > '2023-03-24';

UPDATE klage.text
SET content = REPLACE(content, 'bullet-list', 'ul')
WHERE created > '2023-03-24';

UPDATE klage.text
SET content = REPLACE(content, 'numbered-list', 'ol')
WHERE created > '2023-03-24';

UPDATE klage.text
SET content = REPLACE(content, 'list-item-container', 'lic')
WHERE created > '2023-03-24';

UPDATE klage.text
SET content = REPLACE(content, 'list-item', 'li')
WHERE created > '2023-03-24';

UPDATE klage.text
SET content = REPLACE(content, 'textAlign', 'align')
WHERE created > '2023-03-24';

UPDATE klage.text
SET content = REPLACE(content, 'text-align-left', 'left')
WHERE created > '2023-03-24';

UPDATE klage.text
SET content = REPLACE(content, 'text-align-right', 'right')
WHERE created > '2023-03-24';
