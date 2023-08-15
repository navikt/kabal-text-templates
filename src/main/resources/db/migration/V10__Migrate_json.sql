UPDATE klage.text
SET content = REPLACE(content, 'heading-one', 'h1');

UPDATE klage.text
SET content = REPLACE(content, 'heading-two', 'h2');

UPDATE klage.text
SET content = REPLACE(content, 'heading-three', 'h3');

UPDATE klage.text
SET content = REPLACE(content, 'heading-four', 'h4');

UPDATE klage.text
SET content = REPLACE(content, 'paragraph', 'p');

UPDATE klage.text
SET content = REPLACE(content, 'bullet-list', 'ul');

UPDATE klage.text
SET content = REPLACE(content, 'numbered-list', 'ol');

UPDATE klage.text
SET content = REPLACE(content, 'list-item-container', 'lic');

UPDATE klage.text
SET content = REPLACE(content, 'list-item', 'li');

UPDATE klage.text
SET content = REPLACE(content, 'textAlign', 'align');

UPDATE klage.text
SET content = REPLACE(content, 'text-align-left', 'left');

UPDATE klage.text
SET content = REPLACE(content, 'text-align-right', 'right');
