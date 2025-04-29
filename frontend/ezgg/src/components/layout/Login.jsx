import React, {useState} from 'react';
import styled from '@emotion/styled';
import {Link, useNavigate} from 'react-router-dom';
import axios from 'axios';

const Container = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: calc(100vh - 64px);
    padding: 2rem;
    background: #0F0F0F;
`;

const FormContainer = styled.div`
    width: 100%;
    max-width: 400px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 12px;
    padding: 2rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
`;

const Title = styled.h1`
    color: white;
    font-size: 2rem;
    font-weight: 800;
    margin-bottom: 2rem;
    text-align: center;
`;

const Form = styled.form`
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
`;

const InputGroup = styled.div`
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
`;

const Label = styled.label`
    color: white;
    font-size: 0.9rem;
`;

const Input = styled.input`
    padding: 0.8rem;
    background: rgba(255, 255, 255, 0.1);
    border: none;
    border-radius: 4px;
    color: white;
    font-size: 1rem;

    &:focus {
        outline: none;
        background: rgba(255, 255, 255, 0.15);
    }

    &::placeholder {
        color: rgba(255, 255, 255, 0.5);
    }
`;

const ErrorMessage = styled.span`
    color: #FF416C;
    font-size: 0.8rem;
    margin-top: 0.2rem;
`;

const SubmitButton = styled.button`
    padding: 1rem;
    background: #FF416C;
    border: none;
    border-radius: 4px;
    color: white;
    font-size: 1rem;
    font-weight: 500;
    cursor: pointer;
    transition: opacity 0.2s;
    opacity: ${props => props.disabled ? 0.5 : 1};
    pointer-events: ${props => props.disabled ? 'none' : 'auto'};

    &:hover {
        opacity: 0.9;
    }
`;

const JoinLink = styled(Link)`
    display: block;
    text-align: center;
    color: #FF416C;
    font-size: 0.9rem;
    text-decoration: none;
    padding: 0.5rem;
    margin-top: 1rem;

    &:hover {
        text-decoration: underline;
    }
`;

const Login = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        username: '',
        password: '',
    });

    const [errors, setErrors] = useState({
        username: '',
        password: '',
    });

    const validateForm = () => {
        let isValid = true;
        const newErrors = {
            username: '',
            password: '',
        };

        if (!formData.username) {
            newErrors.username = '아이디를 입력해주세요';
            isValid = false;
        }

        if (!formData.password) {
            newErrors.password = '비밀번호를 입력해주세요';
            isValid = false;
        }

        setErrors(newErrors);
        return isValid;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validateForm()) {
            return;
        }
        console.log('Form submitted:', formData);

        try {
            const response = await axios.post('http://localhost:8888/login', {
                memberUsername: formData.username,
                password: formData.password
            }, {
                withCredentials: true
            });

            const token = response.headers['Authorization'];
            if (response.status === 200) {
                // 로그인 성공 후, JWT 토큰을 로컬 스토리지에 저장
                localStorage.setItem('token', token);
                // 로그인 성공 후, 홈 페이지로 리다이렉트
                navigate('/');
            }
        } catch (error) {
            alert('로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.');
            console.error('Login error:', error);
        }
    };

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        setErrors(prev => ({
            ...prev,
            [name]: ''
        }));
    };

    return (
        <Container>
            <FormContainer>
                <Title>로그인</Title>
                <Form onSubmit={handleSubmit}>
                    <InputGroup>
                        <Label>아이디</Label>
                        <Input
                            type="text"
                            name="username"
                            placeholder="아이디를 입력하세요"
                            value={formData.username}
                            onChange={handleChange}
                        />
                        {errors.username && <ErrorMessage>{errors.username}</ErrorMessage>}
                    </InputGroup>
                    <InputGroup>
                        <Label>비밀번호</Label>
                        <Input
                            type="password"
                            name="password"
                            placeholder="비밀번호를 입력하세요"
                            value={formData.password}
                            onChange={handleChange}
                        />
                        {errors.password && <ErrorMessage>{errors.password}</ErrorMessage>}
                    </InputGroup>
                    <SubmitButton type="submit">로그인</SubmitButton>
                </Form>
                <JoinLink to="/join">회원가입하기</JoinLink>
            </FormContainer>
        </Container>
    );
};

export default Login; 
