import React, {useState} from 'react';
import styled from '@emotion/styled';
import axios from 'axios';
import {Link, useNavigate} from 'react-router-dom';

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
    max-width: 600px;
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

const LoginLink = styled(Link)`
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


const Join = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        memberUsername: '',
        password: '',
        confirmPassword: '',
        email: '',
        riotUsername: '',
        riotTag: '',
    });

    const [isLoading, setIsLoading] = useState(false);
    const [errors, setErrors] = useState({
        memberUsername: '',
        password: '',
        confirmPassword: '',
        email: '',
        riotUsername: '',
        riotTag: '',
    });

    const validateForm = () => {
        let isValid = true;
        const newErrors = {
            memberUsername: '',
            password: '',
            confirmPassword: '',
            email: '',
            riotUsername: '',
            riotTag: '',
        };

        if (!formData.memberUsername) {
            newErrors.memberUsername = '아이디를 입력해주세요';
            isValid = false;
        } else if (formData.memberUsername.length < 4) {
            newErrors.memberUsername = '아이디는 4자 이상이어야 합니다';
            isValid = false;
        }

        if (!formData.password) {
            newErrors.password = '비밀번호를 입력해주세요';
            isValid = false;
        } else if (formData.password.length < 6) {
            newErrors.password = '비밀번호는 6자 이상이어야 합니다';
            isValid = false;
        }

        if (!formData.confirmPassword) {
            newErrors.confirmPassword = '비밀번호 확인을 입력해주세요';
            isValid = false;
        } else if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = '비밀번호가 일치하지 않습니다';
            isValid = false;
        }

        if (!formData.email) {
            newErrors.email = '이메일을 입력해주세요';
            isValid = false;
        } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = '올바른 이메일 형식이 아닙니다';
            isValid = false;
        }

        if (!formData.riotUsername) {
            newErrors.riotUsername = '라이엇 아이디를 입력해주세요';
            isValid = false;
        }

        if (!formData.riotTag) {
            newErrors.riotTag = '태그를 입력해주세요';
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

        console.log('회원가입 데이터:', formData);
        setIsLoading(true);

        try {
            const response = await axios.post('http://localhost:8888/auth/signup', formData);
            console.log('회원가입 성공:', response.data);
            alert('회원가입에 성공했습니다!');
            navigate("/login");
        } catch (error) {
            console.error('회원가입 오류:', error);
            const errorMsg = error.response?.data?.message || '회원가입 중 오류가 발생했습니다.';
            alert(errorMsg);
        } finally {
            setIsLoading(false);
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
                <Title>회원가입</Title>
                <Form onSubmit={handleSubmit}>
                    <InputGroup>
                        <Label>아이디</Label>
                        <Input
                            type="text"
                            name="memberUsername"
                            placeholder="아이디를 입력하세요"
                            value={formData.memberUsername}
                            onChange={handleChange}
                            required
                            disabled={isLoading}
                        />
                        {errors.memberUsername && <ErrorMessage>{errors.memberUsername}</ErrorMessage>}
                    </InputGroup>
                    <InputGroup>
                        <Label>이메일</Label>
                        <Input
                            type="email"
                            name="email"
                            placeholder="이메일을 입력하세요"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            disabled={isLoading}
                        />
                        {errors.email && <ErrorMessage>{errors.email}</ErrorMessage>}
                    </InputGroup>
                    <InputGroup>
                        <Label>비밀번호</Label>
                        <Input
                            type="password"
                            name="password"
                            placeholder="비밀번호를 입력하세요 (6~20자의 영문 대소문자, 숫자)"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            disabled={isLoading}
                        />
                        {errors.password && <ErrorMessage>{errors.password}</ErrorMessage>}
                    </InputGroup>
                    <InputGroup>
                        <Label>비밀번호 확인</Label>
                        <Input
                            type="password"
                            name="confirmPassword"
                            placeholder="비밀번호를 다시 입력하세요 (6~20자의 영문 대소문자, 숫자)"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            required
                            disabled={isLoading}
                        />
                        {errors.confirmPassword && <ErrorMessage>{errors.confirmPassword}</ErrorMessage>}
                    </InputGroup>
                    <InputGroup>
                        <Label>라이엇 아이디</Label>
                        <Input
                            type="text"
                            name="riotUsername"
                            placeholder="라이엇 아이디를 입력하세요"
                            value={formData.riotUsername}
                            onChange={handleChange}
                            required
                            disabled={isLoading}
                        />
                        {errors.riotUsername && <ErrorMessage>{errors.riotUsername}</ErrorMessage>}
                    </InputGroup>
                    <InputGroup>
                        <Label>태그</Label>
                        <Input
                            type="text"
                            name="riotTag"
                            placeholder="태그를 입력하세요 (예: KR1)"
                            value={formData.riotTag}
                            onChange={handleChange}
                            required
                            disabled={isLoading}
                        />
                        {errors.riotTag && <ErrorMessage>{errors.riotTag}</ErrorMessage>}
                    </InputGroup>
                    <SubmitButton type="submit" disabled={isLoading}>
                        {isLoading ? '처리 중...' : '회원가입'}
                    </SubmitButton>
                </Form>
                <LoginLink to="/login">이미 계정이 있으신가요? 로그인</LoginLink>
            </FormContainer>
        </Container>
    );
};


export default Join;
