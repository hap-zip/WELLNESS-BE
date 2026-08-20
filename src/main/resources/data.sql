-- 1. 앞머리 (front-head)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'front-head',
           '이마와 눈 주변의 긴장을 완화하여 무거운 느낌을 덜어주는 부드러운 지압 루틴입니다.',
           3,
           '눈을 직접 누르지 않도록 주의하며, 손가락 끝으로 살짝만 압력을 가하세요.',
           '[
             {"order": 1, "text": "눈을 감고 편안하게 호흡합니다.", "sec": 15},
             {"order": 2, "text": "양손 검지와 중지를 관자놀이에 대고 부드럽게 원을 그리며 마사지합니다.", "sec": 30},
             {"order": 3, "text": "눈썹 안쪽 끝부분(미간)을 엄지로 지그시 5초간 누릅니다.", "sec": 15},
             {"order": 4, "text": "이마 중앙에서 바깥쪽으로 쓸어넘기듯 마사지하며 마무리합니다.", "sec": 30}
           ]'
       );

-- 2. 뒷머리 (back-head)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'back-head',
           '뒷머리와 목이 만나는 지점의 뻣뻣함을 풀어주는 마사지 루틴입니다.',
           3,
           '손가락 끝을 사용해 너무 강하지 않게 부드럽게 원을 그리듯 마사지하세요.',
           '[
             {"order": 1, "text": "눈을 감고 편안하게 호흡을 가다듬습니다.", "sec": 15},
             {"order": 2, "text": "양손의 엄지손가락을 뒷머리와 목이 만나는 움푹 들어간 곳에 댑니다.", "sec": 10},
             {"order": 3, "text": "위쪽을 향해 지그시 누르며 바깥쪽으로 작은 원을 그리며 마사지합니다.", "sec": 60},
             {"order": 4, "text": "손을 내리고 가볍게 목을 좌우로 돌려줍니다.", "sec": 20}
           ]'
       );

-- 3. 뒷목 (back-neck)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'back-neck',
           '뒷목 주변의 뻣뻣한 근육을 부드럽게 이완시키고 긴장을 덜어주는 스트레칭입니다.',
           5,
           '통증이 느껴지지 않는 범위 내에서만 지그시 눌러주세요. 절대 반동을 주지 마세요.',
           '[
             {"order": 1, "text": "바른 자세로 앉아 가슴을 펴고 정면을 응시합니다.", "sec": 10},
             {"order": 2, "text": "양손을 깍지 껴서 뒤통수에 가볍게 얹어줍니다.", "sec": 10},
             {"order": 3, "text": "숨을 내쉬며 턱이 가슴에 닿는 느낌으로 머리를 지그시 아래로 누릅니다.", "sec": 30},
             {"order": 4, "text": "천천히 고개를 들어 원래 자세로 돌아옵니다.", "sec": 10}
           ]'
       );

-- 4. 등 상단 (back-upper)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'back-upper',
           '등 위쪽과 날개뼈 주변의 결림을 풀어주고 부드럽게 이완하는 루틴입니다.',
           5,
           '팔을 과도하게 뻗지 않고 자연스러운 호흡을 유지하세요.',
           '[
             {"order": 1, "text": "바르게 앉거나 서서 양손을 앞으로 나란히 뻗고 깍지를 낍니다.", "sec": 10},
             {"order": 2, "text": "숨을 내쉬며 등을 둥글게 말고, 깍지 낀 손을 최대한 앞으로 밀어냅니다.", "sec": 30},
             {"order": 3, "text": "날개뼈 사이가 멀어지는 느낌에 집중하며 유지합니다.", "sec": 30},
             {"order": 4, "text": "숨을 들이마시며 원래 자세로 돌아와 어깨를 가볍게 털어줍니다.", "sec": 15}
           ]'
       );

-- 5. 허리 (back-lower)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'back-lower',
           '허리와 골반 주변의 뻐근함을 덜어주고 유연성을 높여주는 고양이 자세 루틴입니다.',
           7,
           '허리 디스크가 심하신 분은 허리를 둥글게 마는 동작을 피하거나 가볍게만 진행하세요.',
           '[
             {"order": 1, "text": "바닥에 엎드려 양손과 무릎을 어깨너비로 벌려 네발 기기 자세를 만듭니다.", "sec": 15},
             {"order": 2, "text": "숨을 내쉬며 시선은 배꼽을 향하고 허리를 천장 쪽으로 둥글게 말아 올립니다.", "sec": 30},
             {"order": 3, "text": "숨을 들이마시며 시선은 천장을 향하고 허리를 바닥 쪽으로 오목하게 내립니다.", "sec": 30},
             {"order": 4, "text": "이 동작을 3회 반복한 후, 아기 자세로 휴식합니다.", "sec": 60}
           ]'
       );

-- 6. 가슴 (front-chest)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'front-chest',
           '가슴 앞쪽 근육을 열어주어 호흡을 편안하게 하고 굽은 자세를 펴주는 스트레칭입니다.',
           4,
           '어깨가 위로 으쓱 올라가지 않도록 아래로 끌어내린 상태를 유지하세요.',
           '[
             {"order": 1, "text": "열린 문틈이나 벽 모서리에 섭니다.", "sec": 10},
             {"order": 2, "text": "한쪽 팔을 90도로 굽혀 벽에 대고 몸통을 반대쪽으로 천천히 회전시킵니다.", "sec": 30},
             {"order": 3, "text": "가슴 앞쪽이 늘어나는 것을 느끼며 호흡합니다.", "sec": 30},
             {"order": 4, "text": "반대쪽 가슴도 동일하게 진행합니다.", "sec": 40}
           ]'
       );

-- 7. 복부 (front-abdomen)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'front-abdomen',
           '복부 근육의 긴장을 풀어주고 전면부를 부드럽게 이완하는 코브라 자세 루틴입니다.',
           5,
           '허리에 통증이 느껴진다면 팔꿈치만 바닥에 대는 자세(스핑크스 자세)로 대체하세요.',
           '[
             {"order": 1, "text": "바닥에 배를 대고 엎드려 양손을 가슴 옆 바닥에 짚습니다.", "sec": 15},
             {"order": 2, "text": "숨을 들이마시며 바닥을 밀어내어 상체를 천천히 들어 올립니다.", "sec": 20},
             {"order": 3, "text": "복부 앞면이 길게 늘어나는 것을 느끼며 시선은 정면을 향합니다.", "sec": 30},
             {"order": 4, "text": "숨을 내쉬며 천천히 바닥으로 내려옵니다.", "sec": 15}
           ]'
       );

-- 8. 왼쪽 앞어깨 (front-shoulder-left)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'front-shoulder-left',
           '왼쪽 어깨 앞면의 긴장을 풀어주어 부드러운 움직임을 돕는 스트레칭입니다.',
           4,
           '스트레칭 시 팔이 뒤로 넘어가는 각도를 무리하게 꺾지 마세요.',
           '[
             {"order": 1, "text": "벽이나 의자를 등지고 섭니다.", "sec": 10},
             {"order": 2, "text": "왼손을 뒤로 뻗어 지지대를 잡습니다.", "sec": 10},
             {"order": 3, "text": "몸통을 천천히 앞으로 이동시키며 왼쪽 어깨 앞쪽을 늘려줍니다.", "sec": 30},
             {"order": 4, "text": "긴장을 풀고 천천히 제자리로 돌아옵니다.", "sec": 10}
           ]'
       );

-- 9. 오른쪽 앞어깨 (front-shoulder-right)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'front-shoulder-right',
           '오른쪽 어깨 앞면의 긴장을 풀어주어 부드러운 움직임을 돕는 스트레칭입니다.',
           4,
           '스트레칭 시 팔이 뒤로 넘어가는 각도를 무리하게 꺾지 마세요.',
           '[
             {"order": 1, "text": "벽이나 의자를 등지고 섭니다.", "sec": 10},
             {"order": 2, "text": "오른손을 뒤로 뻗어 지지대를 잡습니다.", "sec": 10},
             {"order": 3, "text": "몸통을 천천히 앞으로 이동시키며 오른쪽 어깨 앞쪽을 늘려줍니다.", "sec": 30},
             {"order": 4, "text": "긴장을 풀고 천천히 제자리로 돌아옵니다.", "sec": 10}
           ]'
       );

-- 10. 왼쪽 등/어깨 (back-shoulder-left)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'back-shoulder-left',
           '뭉친 왼쪽 날개뼈 주변과 어깨 뒷면 근육을 시원하게 풀어주는 스트레칭입니다.',
           4,
           '어깨 관절에 무리가 가지 않도록 팔을 당길 때 몸통이 같이 돌아가지 않게 고정하세요.',
           '[
             {"order": 1, "text": "왼팔을 가슴 앞으로 곧게 뻗어줍니다.", "sec": 10},
             {"order": 2, "text": "오른팔을 구부려 왼팔의 팔꿈치 바깥쪽을 감싸 안습니다.", "sec": 10},
             {"order": 3, "text": "오른팔을 몸통 쪽으로 지그시 당기며 왼쪽 어깨 뒷부분을 늘려줍니다.", "sec": 30},
             {"order": 4, "text": "호흡을 유지하며 긴장을 풀고 원래 자세로 돌아옵니다.", "sec": 10}
           ]'
       );

-- 11. 오른쪽 등/어깨 (back-shoulder-right)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'back-shoulder-right',
           '뭉친 오른쪽 날개뼈 주변과 어깨 뒷면 근육을 시원하게 풀어주는 스트레칭입니다.',
           4,
           '어깨 관절에 무리가 가지 않도록 팔을 당길 때 몸통이 같이 돌아가지 않게 고정하세요.',
           '[
             {"order": 1, "text": "오른팔을 가슴 앞으로 곧게 뻗어줍니다.", "sec": 10},
             {"order": 2, "text": "왼팔을 구부려 오른팔의 팔꿈치 바깥쪽을 감싸 안습니다.", "sec": 10},
             {"order": 3, "text": "왼팔을 몸통 쪽으로 지그시 당기며 오른쪽 어깨 뒷부분을 늘려줍니다.", "sec": 30},
             {"order": 4, "text": "호흡을 유지하며 긴장을 풀고 원래 자세로 돌아옵니다.", "sec": 10}
           ]'
       );

-- 12. 왼쪽 앞팔 (front-arm-left)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'front-arm-left',
           '왼쪽 팔 앞면(전완부)의 뻐근함을 이완시켜 손목과 팔의 피로를 덜어주는 루틴입니다.',
           3,
           '손목이 시큰거리지 않을 정도까지만 손가락을 몸 쪽으로 당겨주세요.',
           '[
             {"order": 1, "text": "왼팔을 앞으로 나란히 뻗고 손바닥이 앞을 향하게 세웁니다.", "sec": 10},
             {"order": 2, "text": "오른손으로 왼손 손가락 전체를 잡습니다.", "sec": 10},
             {"order": 3, "text": "숨을 내쉬며 왼손을 몸 쪽으로 부드럽게 당겨 팔 안쪽을 늘려줍니다.", "sec": 30},
             {"order": 4, "text": "천천히 손을 풀고 손목을 가볍게 돌려줍니다.", "sec": 15}
           ]'
       );

-- 13. 오른쪽 앞팔 (front-arm-right)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'front-arm-right',
           '오른쪽 팔 앞면(전완부)의 뻐근함을 이완시켜 손목과 팔의 피로를 덜어주는 루틴입니다.',
           3,
           '손목이 시큰거리지 않을 정도까지만 손가락을 몸 쪽으로 당겨주세요.',
           '[
             {"order": 1, "text": "오른팔을 앞으로 나란히 뻗고 손바닥이 앞을 향하게 세웁니다.", "sec": 10},
             {"order": 2, "text": "왼손으로 오른손 손가락 전체를 잡습니다.", "sec": 10},
             {"order": 3, "text": "숨을 내쉬며 오른손을 몸 쪽으로 부드럽게 당겨 팔 안쪽을 늘려줍니다.", "sec": 30},
             {"order": 4, "text": "천천히 손을 풀고 손목을 가볍게 돌려줍니다.", "sec": 15}
           ]'
       );

-- 14. 왼쪽 뒷팔 (back-arm-left)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'back-arm-left',
           '왼쪽 팔 뒷면(삼두근)의 긴장을 풀어 팔을 가볍게 해주는 스트레칭입니다.',
           3,
           '허리가 과도하게 꺾이거나 고개가 앞으로 숙여지지 않도록 주의하세요.',
           '[
             {"order": 1, "text": "왼팔을 머리 위로 들어 올린 후 팔꿈치를 굽혀 손끝이 등을 향하게 합니다.", "sec": 10},
             {"order": 2, "text": "오른손으로 왼쪽 팔꿈치를 가볍게 잡습니다.", "sec": 10},
             {"order": 3, "text": "오른손을 이용해 왼쪽 팔꿈치를 머리 뒤쪽으로 지그시 당겨줍니다.", "sec": 30},
             {"order": 4, "text": "팔을 천천히 내리고 어깨를 가볍게 돌려줍니다.", "sec": 15}
           ]'
       );

-- 15. 오른쪽 뒷팔 (back-arm-right)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'back-arm-right',
           '오른쪽 팔 뒷면(삼두근)의 긴장을 풀어 팔을 가볍게 해주는 스트레칭입니다.',
           3,
           '허리가 과도하게 꺾이거나 고개가 앞으로 숙여지지 않도록 주의하세요.',
           '[
             {"order": 1, "text": "오른팔을 머리 위로 들어 올린 후 팔꿈치를 굽혀 손끝이 등을 향하게 합니다.", "sec": 10},
             {"order": 2, "text": "왼손으로 오른쪽 팔꿈치를 가볍게 잡습니다.", "sec": 10},
             {"order": 3, "text": "왼손을 이용해 오른쪽 팔꿈치를 머리 뒤쪽으로 지그시 당겨줍니다.", "sec": 30},
             {"order": 4, "text": "팔을 천천히 내리고 어깨를 가볍게 돌려줍니다.", "sec": 15}
           ]'
       );

-- 16. 왼쪽 앞다리 (front-leg-left)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'front-leg-left',
           '왼쪽 허벅지 앞면을 길게 늘려 하체의 피로를 부드럽게 이완하는 루틴입니다.',
           4,
           '무릎에 무리가 간다면 너무 강하게 당기지 말고, 중심을 잡기 위해 벽을 짚고 진행하세요.',
           '[
             {"order": 1, "text": "바르게 서서 한 손으로 벽이나 의자를 짚어 중심을 잡습니다.", "sec": 10},
             {"order": 2, "text": "왼쪽 무릎을 뒤로 굽혀 왼손으로 발목이나 발등을 잡습니다.", "sec": 10},
             {"order": 3, "text": "뒤꿈치를 엉덩이 쪽으로 지그시 당기며 허벅지 앞쪽을 늘려줍니다.", "sec": 30},
             {"order": 4, "text": "다리를 천천히 내려놓습니다.", "sec": 10}
           ]'
       );

-- 17. 오른쪽 앞다리 (front-leg-right)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'front-leg-right',
           '오른쪽 허벅지 앞면을 길게 늘려 하체의 피로를 부드럽게 이완하는 루틴입니다.',
           4,
           '무릎에 무리가 간다면 너무 강하게 당기지 말고, 중심을 잡기 위해 벽을 짚고 진행하세요.',
           '[
             {"order": 1, "text": "바르게 서서 한 손으로 벽이나 의자를 짚어 중심을 잡습니다.", "sec": 10},
             {"order": 2, "text": "오른쪽 무릎을 뒤로 굽혀 오른손으로 발목이나 발등을 잡습니다.", "sec": 10},
             {"order": 3, "text": "뒤꿈치를 엉덩이 쪽으로 지그시 당기며 허벅지 앞쪽을 늘려줍니다.", "sec": 30},
             {"order": 4, "text": "다리를 천천히 내려놓습니다.", "sec": 10}
           ]'
       );

-- 18. 왼쪽 뒷다리 (back-leg-left)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'back-leg-left',
           '왼쪽 다리 뒷면(햄스트링)을 부드럽게 풀어주어 다리를 가볍게 만들어주는 루틴입니다.',
           5,
           '무릎을 무리하게 펴지 말고, 허벅지 뒷면이 당기는 느낌이 들 정도까지만 진행하세요.',
           '[
             {"order": 1, "text": "바닥에 등을 대고 편안하게 눕습니다.", "sec": 15},
             {"order": 2, "text": "왼쪽 무릎을 가슴 쪽으로 당겨 양손으로 허벅지 뒤쪽을 잡습니다.", "sec": 10},
             {"order": 3, "text": "숨을 내쉬며 왼쪽 발뒤꿈치를 천장 쪽으로 천천히 밀어 올려 무릎을 폅니다.", "sec": 30},
             {"order": 4, "text": "발끝을 몸통 쪽으로 당겨 종아리까지 자극을 느끼며 유지합니다.", "sec": 20},
             {"order": 5, "text": "천천히 다리를 접어 바닥에 내려놓습니다.", "sec": 15}
           ]'
       );

-- 19. 오른쪽 뒷다리 (back-leg-right)
INSERT IGNORE INTO routines (target_area, description, total_duration_minutes, precautions, steps_data)
VALUES (
           'back-leg-right',
           '오른쪽 다리 뒷면(햄스트링)을 부드럽게 풀어주어 다리를 가볍게 만들어주는 루틴입니다.',
           5,
           '무릎을 무리하게 펴지 말고, 허벅지 뒷면이 당기는 느낌이 들 정도까지만 진행하세요.',
           '[
             {"order": 1, "text": "바닥에 등을 대고 편안하게 눕습니다.", "sec": 15},
             {"order": 2, "text": "오른쪽 무릎을 가슴 쪽으로 당겨 양손으로 허벅지 뒤쪽을 잡습니다.", "sec": 10},
             {"order": 3, "text": "숨을 내쉬며 오른쪽 발뒤꿈치를 천장 쪽으로 천천히 밀어 올려 무릎을 폅니다.", "sec": 30},
             {"order": 4, "text": "발끝을 몸통 쪽으로 당겨 종아리까지 자극을 느끼며 유지합니다.", "sec": 20},
             {"order": 5, "text": "천천히 다리를 접어 바닥에 내려놓습니다.", "sec": 15}
           ]'
       );